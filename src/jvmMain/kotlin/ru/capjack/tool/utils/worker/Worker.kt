package ru.capjack.tool.utils.worker

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.collections.ArrayQueue
import ru.capjack.tool.utils.assistant.Assistant

actual open class Worker actual constructor(
	private val assistant: Assistant,
	private val errorHandler: (Throwable) -> Unit
) {
	private val lock = Any()
	private val queue = ArrayQueue<() -> Unit>()
	private val nextTaskFn = ::nextTask
	private var errorCatching = false
	
	@Volatile
	private var workingThread: Long = -1
	@Volatile
	private var _working = false
	@Volatile
	private var _relaxed = true
	
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
		catch (e: Throwable) {
			catchError(e)
		}
	}
	
	fun catchError(error: Throwable) {
		if (errorCatching) {
			ownLogger.error("Nested error", error)
		}
		else {
			errorCatching = true
			try {
				errorHandler.invoke(error)
			}
			catch (e: Throwable) {
				ownLogger.error("Nested error on catching", e)
			}
			errorCatching = false
		}
	}
	
	private fun work(task: () -> Unit) {
		assignWorkingThread()
		protect(task)
		loseWorkingThread()
		scheduleNextTask()
	}
	
	private fun scheduleNextTask() {
		assistant.execute(nextTaskFn)
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