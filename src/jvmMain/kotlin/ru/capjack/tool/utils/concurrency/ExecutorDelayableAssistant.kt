package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ExecutorDelayableAssistant(executor: ScheduledExecutorService) : AbstractExecutorAssistant<ScheduledExecutorService>(executor), DelayableAssistant {
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		return executor.schedule(code, delayMillis.toLong(), TimeUnit.MILLISECONDS).asCancelable()
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		val delay = delayMillis.toLong()
		return executor.scheduleAtFixedRate(code, delay, delay, TimeUnit.MILLISECONDS).asCancelable()
	}
}