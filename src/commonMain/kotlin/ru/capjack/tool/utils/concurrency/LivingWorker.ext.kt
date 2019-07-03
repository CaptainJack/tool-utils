package ru.capjack.tool.utils.concurrency

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
			action()
		}
		finally {
			release()
		}
		return true
	}
	return false
}