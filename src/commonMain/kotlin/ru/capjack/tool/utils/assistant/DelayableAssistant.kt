package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable

interface DelayableAssistant : Assistant {
	fun schedule(delayMillis: Int, code: () -> Unit): Cancelable
	
	fun repeat(delayMillis: Int, code: () -> Unit): Cancelable
}