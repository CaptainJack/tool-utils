package ru.capjack.tool.utils

interface ErrorCatcher {
	val catchErrorFunction: (error: dynamic) -> Unit
	
	fun catchError(error: dynamic)
}
