package ru.capjack.tool.utils

import ru.capjack.tool.lang.asThrowable
import ru.capjack.tool.logging.Logger
import ru.capjack.tool.logging.Logging
import ru.capjack.tool.logging.getLogger

class LoggingErrorCatcher(
	private val logger: Logger = Logging.getLogger<LoggingErrorCatcher>()
) : ErrorCatcher {
	
	override fun catchError(error: dynamic) {
		logger.error("Uncaught error", asThrowable(error))
	}
}