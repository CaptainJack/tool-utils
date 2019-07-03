package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

internal class ScheduledExecutorImpl(
	service: ScheduledExecutorService
) : ExecutorImpl<ScheduledExecutorService>(service), ScheduledExecutor {
	
	override fun schedule(delay: Int, fn: () -> Unit): Cancelable {
		return service.schedule(fn, delay.toLong(), TimeUnit.MILLISECONDS).asCancelable()
	}
	
	override fun scheduleRepeat(delay: Int, fn: () -> Unit): Cancelable {
		return service.scheduleAtFixedRate(fn, delay.toLong(), delay.toLong(), TimeUnit.MILLISECONDS).asCancelable()
	}
}