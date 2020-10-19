package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.TimeUnit

actual interface TemporalAssistant : Assistant {
	actual fun schedule(delayMillis: Int, code: () -> Unit): Cancelable
	actual fun repeat(delayMillis: Int, code: () -> Unit): Cancelable
	
	fun schedule(delay: Int, unit: TimeUnit, code: () -> Unit): Cancelable
	
	fun repeat(delay: Int, unit: TimeUnit, code: () -> Unit): Cancelable
}