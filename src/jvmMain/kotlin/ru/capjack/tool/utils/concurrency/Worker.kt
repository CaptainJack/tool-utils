package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.logging.ownLogger
import java.util.*

actual open class Worker actual constructor(
	private val executor: Executor
) {
	
	private val lock = Any()
	private val queue = LinkedList<() -> Unit>()
	
	@Volatile
	private var working = false
	@Volatile
	private var workingThread: Long = -1
	
	private val assignTask = Runnable {
		assignWorkingThread()
		processQueue()
	}
	
	actual val accessible: Boolean
		get() = workingThread == defineCurrentThread()
	
	actual fun execute(task: () -> Unit) {
		synchronized(lock) {
			if (working) {
				queue.add(task)
				return
			}
			working = true
		}
		
		assignWorkingThread()
		work(task)
		processQueue()
	}
	
	actual fun defer(task: () -> Unit) {
		synchronized(lock) {
			queue.add(task)
			if (working) {
				return
			}
			working = true
		}
		
		executor.execute(assignTask)
	}
	
	actual fun capture(): Boolean {
		synchronized(lock) {
			if (working) {
				return accessible
			}
			working = true
		}
		assignWorkingThread()
		return true
	}
	
	actual fun release() {
		synchronized(lock) {
			if (!working || !accessible) {
				return
			}
		}
		processQueue()
	}
	
	protected open fun work(task: () -> Unit) {
		try {
			task()
		}
		catch (e: Throwable) {
			ownLogger.error("Uncaught exception", e)
		}
	}
	
	private fun processQueue() {
		while (true) {
			val task: () -> Unit
			synchronized(lock) {
				queue.poll().also {
					if (it == null) {
						loseWorkingThread()
						working = false
						return
					}
					task = it
				}
			}
			work(task)
		}
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