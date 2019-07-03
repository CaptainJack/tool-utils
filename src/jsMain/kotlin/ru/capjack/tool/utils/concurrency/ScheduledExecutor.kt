package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

actual interface ScheduledExecutor : Executor {
	actual fun schedule(delay: Int, fn: () -> Unit): Cancelable
	
	actual fun scheduleRepeat(delay: Int, fn: () -> Unit): Cancelable
}