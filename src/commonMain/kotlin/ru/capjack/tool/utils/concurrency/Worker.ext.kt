package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.lang.alsoElse
import ru.capjack.tool.lang.alsoIf

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

inline fun Worker.access(action: () -> Unit): Boolean {
	return accessible.alsoIf {
		action()
	}
}

inline fun Worker.accessOrExecute(crossinline action: () -> Unit) {
	access(action).alsoElse {
		execute { action() }
	}
}

inline fun Worker.accessOrDefer(crossinline action: () -> Unit) {
	access(action).alsoElse {
		defer { action() }
	}
}