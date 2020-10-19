package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.asCancelable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ExecutorTemporalAssistant(executor: ScheduledExecutorService) : AbstractExecutorAssistant<ScheduledExecutorService>(executor), TemporalAssistant {
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		return schedule(delayMillis, TimeUnit.MILLISECONDS, code)
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		val delay = delayMillis.toLong()
		return executor.scheduleAtFixedRate(code, delay, delay, TimeUnit.MILLISECONDS).asCancelable()
	}
	
	override fun schedule(delay: Int, unit: TimeUnit, code: () -> Unit): Cancelable {
		return executor.schedule(code, delay.toLong(), unit).asCancelable()
	}
	
	override fun repeat(delay: Int, unit: TimeUnit, code: () -> Unit): Cancelable {
		val d = delay.toLong()
		return executor.scheduleAtFixedRate(code, d, d, unit).asCancelable()
	}
}