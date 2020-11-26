package ru.capjack.tool.utils

fun interface ErrorHandler {
	fun handleError(error: Throwable)
}