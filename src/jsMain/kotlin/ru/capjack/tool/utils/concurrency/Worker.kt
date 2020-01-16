package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.lang.asThrowable
import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.collections.ArrayQueue

actual open class Worker actual constructor(
	private val assistant: Assistant,
	private val errorHandler: (Throwable) -> Unit
) {
	private var _working = false
	private var queue = ArrayQueue<() -> Unit>()
	private var errorCatching = false
	private val nextTaskFn = ::nextTask
	
	actual val working: Boolean
		get() = _working
	
	actual val accessible: Boolean
		get() = _working
	
	actual val relaxed: Boolean
		get() = queue.isEmpty()
	
	
	actual fun execute(task: () -> Unit) {
		if (_working) {
			queue.add(task)
		}
		else {
			_working = true
			work(task)
		}
	}
	
	actual fun defer(task: () -> Unit) {
		queue.add(task)
		if (!_working) {
			_working = true
			scheduleNextTask()
		}
	}
	
	actual fun capture(): Boolean {
		_working = true
		return true
	}
	
	actual fun release() {
		if (_working) {
			scheduleNextTask()
		}
	}
	
	actual inline fun protect(task: () -> Unit) {
		try {
			task()
		}
		catch (e: dynamic) {
			catchError(asThrowable(e))
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
				ownLogger.error("Nested error on catching", asThrowable(e))
			}
			errorCatching = false
		}
	}
	
	private fun work(task: () -> Unit) {
		protect(task)
		scheduleNextTask()
	}
	
	private fun scheduleNextTask() {
		assistant.execute(nextTaskFn)
	}
	
	private fun nextTask() {
		val task = queue.poll()
		if (task == null) {
			_working = false
			return
		}
		work(task)
	}
}