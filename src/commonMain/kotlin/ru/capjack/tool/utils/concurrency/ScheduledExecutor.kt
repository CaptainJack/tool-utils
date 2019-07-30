package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

expect interface ScheduledExecutor : Executor {
	fun schedule(delayMillis: Int, fn: () -> Unit): Cancelable
	
	fun repeat(delayMillis: Int, fn: () -> Unit): Cancelable
}