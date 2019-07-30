package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

open class ScheduledExecutorImpl(
	private val service: ScheduledExecutorService
) : ExecutorImpl(service), ScheduledExecutor {
	
	override fun schedule(delayMillis: Int, fn: () -> Unit): Cancelable {
		return service.schedule(fn, delayMillis.toLong(), TimeUnit.MILLISECONDS).asCancelable()
	}
	
	override fun repeat(delayMillis: Int, fn: () -> Unit): Cancelable {
		val delay = delayMillis.toLong()
		return service.scheduleAtFixedRate(fn, delay, delay, TimeUnit.MILLISECONDS).asCancelable()
	}
}