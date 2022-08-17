package ru.capjack.tool.utils.worker

import ru.capjack.tool.lang.alsoFalse
import ru.capjack.tool.lang.alsoTrue
import ru.capjack.tool.utils.ErrorHandler
import ru.capjack.tool.utils.assistant.Assistant
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

expect open class Worker(assistant: Assistant, errorHandler: ErrorHandler? = null) {
	val working: Boolean
	
	val accessible: Boolean
	
	val relaxed: Boolean
	
	fun execute(task: () -> Unit)
	
	fun defer(task: () -> Unit)
	
	fun capture(): Boolean
	
	fun release()
	
	inline fun protect(task: () -> Unit)
}


inline fun Worker.withCapture(action: () -> Unit): Boolean {
	if (capture()) {
		try {
			protect(action)
		}
		finally {
			release()
		}
		return true
	}
	return false
}

inline fun Worker.access(action: () -> Unit): Boolean {
	return accessible.alsoTrue {
		protect(action)
	}
}

inline fun Worker.accessOrExecute(crossinline action: () -> Unit) {
	access(action).alsoFalse {
		execute { action() }
	}
}

inline fun Worker.accessOrDefer(crossinline action: () -> Unit) {
	access(action).alsoFalse {
		defer { action() }
	}
}

suspend inline fun <R> Worker.suspendExecute(crossinline task: () -> R): R {
	return suspendCoroutine { continuation ->
		execute {
			try {
				continuation.resume(task())
			}
			catch (e: Throwable) {
				continuation.resumeWithException(e)
			}
		}
	}
}