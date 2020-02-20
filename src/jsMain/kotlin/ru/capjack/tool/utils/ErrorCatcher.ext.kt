package ru.capjack.tool.utils

inline fun ErrorCatcher.protect(code: () -> Unit) {
	try {
		code()
	}
	catch (e: dynamic) {
		catchError(e)
	}
}

inline fun ErrorCatcher.protect(code: () -> Unit, catch: () -> Unit) {
	try {
		code()
	}
	catch (e: dynamic) {
		catchError(e)
		catch()
	}
}