package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.lang.alsoFalse
import ru.capjack.tool.lang.alsoTrue

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