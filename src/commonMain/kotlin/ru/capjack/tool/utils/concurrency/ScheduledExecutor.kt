package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

expect interface ScheduledExecutor : Executor {
	fun schedule(delay: Int, fn: () -> Unit): Cancelable
	
	fun scheduleRepeat(delay: Int, fn: () -> Unit): Cancelable
}