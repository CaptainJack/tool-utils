package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable

actual interface TemporalAssistant : Assistant {
	actual fun schedule(delayMillis: Int, code: () -> Unit): Cancelable
	actual fun repeat(delayMillis: Int, code: () -> Unit): Cancelable
}