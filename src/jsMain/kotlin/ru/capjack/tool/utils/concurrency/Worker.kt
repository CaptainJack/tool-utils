package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.lang.asThrowable
import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.CallableFunction0
import ru.capjack.tool.utils.collections.ArrayQueue

actual open class Worker actual constructor(
	private val executor: Executor,
	private val errorHandler: (Throwable) -> Unit
) {
	private var queue = ArrayQueue<() -> Unit>()
	private var processQueueTask = CallableFunction0(::processQueue)
	private var working = false
	
	actual val accessible: Boolean
		get() = working
	
	actual fun execute(task: () -> Unit) {
		if (working) {
			queue.add(task)
		}
		else {
			working = true
			work(task)
			processQueue()
		}
	}
	
	actual fun defer(task: () -> Unit) {
		queue.add(task)
		if (!working) {
			working = true
			executor.execute(processQueueTask)
		}
	}
	
	actual fun capture(): Boolean {
		working = true
		return true
	}
	
	actual fun release() {
		if (working) {
			processQueue()
		}
	}
	
	private fun work(task: () -> Unit) {
		try {
			task()
		}
		catch (t: dynamic) {
			try {
				errorHandler(asThrowable(t))
			}
			catch (e: dynamic) {
				ownLogger.error("Uncaught error", asThrowable(e))
			}
		}
	}
	
	private fun processQueue() {
		while (true) {
			val task = queue.poll()
			if (task == null) {
				working = false
				break
			}
			work(task)
		}
	}
}