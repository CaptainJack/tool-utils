package ru.capjack.tool.utils.assistant

import ru.capjack.tool.logging.Logging
import ru.capjack.tool.logging.getLogger

internal class SafeCode(private val code: () -> Unit) : Runnable {
	override fun run() {
		try {
			code()
		}
		catch (e: Throwable) {
			Logging.getLogger<Assistant>().error("Uncaught throwable in $code", e)
		}
	}
}