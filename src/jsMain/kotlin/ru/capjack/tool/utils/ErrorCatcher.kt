package ru.capjack.tool.utils

interface ErrorCatcher {
	val catchErrorFunction: (dynamic) -> Throwable
	
	fun catchError(error: dynamic): Throwable
}
