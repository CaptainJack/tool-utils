package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

actual interface ScheduledExecutor : Executor {
	actual fun schedule(delayMillis: Int, fn: () -> Unit): Cancelable
	
	actual fun repeat(delayMillis: Int, fn: () -> Unit): Cancelable
}