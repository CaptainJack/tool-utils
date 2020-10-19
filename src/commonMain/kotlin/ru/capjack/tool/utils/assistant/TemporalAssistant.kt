package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable

expect interface TemporalAssistant : Assistant {
	fun schedule(delayMillis: Int, code: () -> Unit): Cancelable
	
	fun repeat(delayMillis: Int, code: () -> Unit): Cancelable
}