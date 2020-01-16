package ru.capjack.tool.utils

import ru.capjack.tool.lang.asThrowable
import ru.capjack.tool.logging.Logger
import ru.capjack.tool.logging.Logging
import ru.capjack.tool.logging.getLogger

class LoggingErrorCatcher(
	private val logger: Logger = Logging.getLogger<LoggingErrorCatcher>()
) : ErrorCatcher {
	
	override val catchErrorFunction = ::catchError
	
	override fun catchError(error: dynamic): Throwable {
		return asThrowable(error).also {
			logger.error("Uncaught error", it)
		}
	}
}