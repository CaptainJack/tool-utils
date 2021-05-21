package ru.capjack.tool.utils.assistant

import ru.capjack.tool.logging.Logging
import ru.capjack.tool.logging.getLogger

internal class SafeCode(private val code: () -> Unit) : Runnable {
	override fun run() {
		safeCode(code)
	}
}

internal inline fun safeCode(code: () -> Unit) {
	try {
		code()
	}
	catch (e: Throwable) {
		Logging.getLogger<Assistant>().error("Uncaught throwable", e)
	}
}