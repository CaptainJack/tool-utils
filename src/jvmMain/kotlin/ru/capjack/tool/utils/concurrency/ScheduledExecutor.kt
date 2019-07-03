package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ScheduledExecutorService

actual interface ScheduledExecutor : Executor {
	actual fun schedule(delay: Int, fn: () -> Unit): Cancelable
	
	actual fun scheduleRepeat(delay: Int, fn: () -> Unit): Cancelable
	
	companion object {
		operator fun invoke(service: ScheduledExecutorService): ScheduledExecutor {
			return ScheduledExecutorImpl(service)
		}
	}
}