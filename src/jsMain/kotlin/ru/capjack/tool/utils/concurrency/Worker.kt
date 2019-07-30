package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.lang.asThrowable
import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.collections.ArrayQueue

actual open class Worker actual constructor(
	executor: Executor,
	private val errorHandler: (Throwable) -> Unit
) {
	private var queue = ArrayQueue<() -> Unit>()
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
		if (working) {
			queue.add(task)
		}
	}
	
	actual fun capture(): Boolean {
		TODO("not implemented")
	}
	
	actual fun release() {
	}
	
	protected open fun work(task: () -> Unit) {
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