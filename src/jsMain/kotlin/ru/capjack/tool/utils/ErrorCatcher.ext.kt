package ru.capjack.tool.utils

inline fun ErrorCatcher.protect(code: () -> Unit) {
	try {
		code()
	}
	catch (e: dynamic) {
		catch(e)
	}
}

inline fun <R> ErrorCatcher.watch(code: () -> R): R {
	try {
		return code()
	}
	catch (e: dynamic) {
		throw catch(e)
	}
}
