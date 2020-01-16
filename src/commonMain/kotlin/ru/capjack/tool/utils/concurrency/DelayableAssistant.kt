package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

interface DelayableAssistant : Assistant {
	fun schedule(delayMillis: Int, code: () -> Unit): Cancelable
	
	fun repeat(delayMillis: Int, code: () -> Unit): Cancelable
}