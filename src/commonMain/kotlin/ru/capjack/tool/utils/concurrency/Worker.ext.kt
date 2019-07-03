package ru.capjack.tool.utils.concurrency


inline fun Worker.withCapture(action: () -> Unit): Boolean {
	if (capture()) {
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