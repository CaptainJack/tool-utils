package ru.capjack.tool.utils

interface ErrorCatcher {
	val catchFunction: (dynamic) -> Throwable
	
	fun catch(error: dynamic): Throwable
}
