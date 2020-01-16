package ru.capjack.tool.utils

inline fun ErrorCatcher.protect(code: () -> Unit) {
	try {
		code()
	}
	catch (e: dynamic) {
		@Suppress("ThrowableNotThrown")
		catchError(e)
	}
}