package ru.capjack.tool.utils.worker

import ru.capjack.tool.lang.alsoTrue
import ru.capjack.tool.utils.ErrorHandler
import ru.capjack.tool.utils.assistant.Assistant
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Volatile

class LivingWorker(assistant: Assistant, errorHandler: ErrorHandler? = null) : Worker(assistant, errorHandler) {
	@Volatile
	private var _alive = true
	
	val alive
		get() = _alive
	
	fun die() {
		if (_alive) {
			if (accessible) {
				_alive = false
			}
			else {
				execute(::die)
			}
		}
	}
}


inline fun LivingWorker.accessOnLive(action: () -> Unit): Boolean {
	return (alive && accessible).alsoTrue {
		protect(action)
	}
}

inline fun LivingWorker.executeOnLive(crossinline task: () -> Unit) {
	if (alive) {
		execute {
			if (alive) {
				task()
			}
		}
	}
}

inline fun LivingWorker.deferOnLive(crossinline task: () -> Unit) {
	if (alive) {
		defer {
			if (alive) {
				task()
			}
		}
	}
}

fun LivingWorker.captureOnLive(): Boolean {
	if (alive && capture()) {
		if (alive) {
			return true
		}
		release()
	}
	return false
}

inline fun LivingWorker.withCaptureOnLive(action: () -> Unit): Boolean {
	if (captureOnLive()) {
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

inline fun LivingWorker.accessOrExecuteOnLive(crossinline action: () -> Unit) {
	if (alive) {
		if (accessible) {
			protect(action)
		}
		else {
			execute {
				if (alive) {
					action()
				}
			}
		}
	}
}

inline fun LivingWorker.accessOrExecuteOnLive(onAccess: () -> Unit, crossinline onExecute: () -> Unit) {
	if (alive) {
		if (accessible) {
			protect(onAccess)
		}
		else {
			execute {
				if (alive) {
					onExecute()
				}
			}
		}
	}
}

inline fun LivingWorker.accessOrDeferOnLive(crossinline action: () -> Unit) {
	if (alive) {
		if (accessible) {
			protect(action)
		}
		else {
			defer {
				if (alive) {
					action()
				}
			}
		}
	}
}

inline fun LivingWorker.accessOrDeferOnLive(onAccess: () -> Unit, crossinline onExecute: () -> Unit) {
	if (alive) {
		if (accessible) {
			protect(onAccess)
		}
		else {
			defer {
				if (alive) {
					onExecute()
				}
			}
		}
	}
}

suspend inline fun <R> LivingWorker.suspendExecuteOnLive(crossinline task: () -> R): R {
	return suspendCoroutine { continuation ->
		if (alive) {
			execute {
				if (alive) {
					try {
						continuation.resume(task())
					}
					catch (e: Throwable) {
						continuation.resumeWithException(e)
					}
				}
				else {
					continuation.resumeWithException(IllegalStateException("Worker is die"))
				}
			}
		}
		else {
			continuation.resumeWithException(IllegalStateException("Worker is die"))
		}
	}
}