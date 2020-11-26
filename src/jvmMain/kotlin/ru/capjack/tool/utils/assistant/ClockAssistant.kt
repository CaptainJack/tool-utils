package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import java.time.Clock
import java.time.LocalDateTime

interface ClockAssistant : TemporalAssistant {
	val clock: Clock
	
	fun schedule(point: LocalDateTime, code: () -> Unit): Cancelable
}