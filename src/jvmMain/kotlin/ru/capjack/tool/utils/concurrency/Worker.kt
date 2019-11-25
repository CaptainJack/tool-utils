package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.collections.ArrayQueue

actual open class Worker actual constructor(
	private val executor: Executor,
	val errorHandler: (Throwable) -> Unit
) {
	
	private val lock = Any()
	private val queue = ArrayQueue<() -> Unit>()
	
	@Volatile
	private var _working = false
	@Volatile
	private var workingThread: Long = -1
	@Volatile
	private var _relaxed = true
	
	private val nextTaskFn = Runnable { nextTask() }
	
	actual val working: Boolean
		get() = _working
	
	actual val accessible: Boolean
		get() = workingThread == defineCurrentThread()
	
	actual val relaxed: Boolean
		get() = _relaxed
	
	actual fun execute(task: () -> Unit) {
		synchronized(lock) {
			if (_working) {
				_relaxed = false
				queue.add(task)
				return
			}
			_working = true
		}
		work(task)
	}
	
	actual fun defer(task: () -> Unit) {
		synchronized(lock) {
			_relaxed = false
			queue.add(task)
			if (_working) {
				return
			}
			_working = true
		}
		scheduleNextTask()
	}
	
	actual fun capture(): Boolean {
		synchronized(lock) {
			if (_working) {
				return accessible
			}
			_working = true
		}
		assignWorkingThread()
		return true
	}
	
	actual fun release() {
		synchronized(lock) {
			if (!_working || !accessible) {
				return
			}
		}
		loseWorkingThread()
		scheduleNextTask()
	}
	
	actual inline fun protect(task: () -> Unit) {
		try {
			task()
		}
		catch (t: Throwable) {
			try {
				errorHandler.invoke(t)
			}
			catch (e: Throwable) {
				ownLogger.error("Uncaught error", e)
			}
		}
	}
	
	private fun work(task: () -> Unit) {
		assignWorkingThread()
		protect(task)
		loseWorkingThread()
		scheduleNextTask()
	}
	
	private fun scheduleNextTask() {
		executor.execute(nextTaskFn)
	}
	
	private fun nextTask() {
		val task: () -> Unit
		synchronized(lock) {
			queue.poll().also {
				if (it == null) {
					_working = false
					return
				}
				task = it
			}
			_relaxed = queue.isEmpty()
		}
		work(task)
	}
	
	private fun defineCurrentThread(): Long {
		return Thread.currentThread().id
	}
	
	private fun assignWorkingThread() {
		workingThread = defineCurrentThread()
	}
	
	private fun loseWorkingThread() {
		workingThread = -1
	}
}