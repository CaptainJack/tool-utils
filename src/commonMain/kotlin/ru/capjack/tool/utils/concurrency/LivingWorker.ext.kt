package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.lang.alsoTrue

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
