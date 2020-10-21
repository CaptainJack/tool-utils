package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import java.time.LocalDateTime

interface ClockAssistant : Assistant {
	fun schedule(point: LocalDateTime, code: () -> Unit): Cancelable
}