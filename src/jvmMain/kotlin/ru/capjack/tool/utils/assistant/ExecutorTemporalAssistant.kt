package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.asCancelable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

open class ExecutorTemporalAssistant(executor: ScheduledExecutorService) : AbstractExecutorAssistant<ScheduledExecutorService>(executor), TemporalAssistant {
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		return schedule(delayMillis.toLong(), TimeUnit.MILLISECONDS, code)
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		val delay = delayMillis.toLong()
		return executor.scheduleAtFixedRate(SafeCode(code), delay, delay, TimeUnit.MILLISECONDS).asCancelable()
	}
	
	override fun schedule(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		return executor.schedule(SafeCode(code), delay, unit).asCancelable()
	}
	
	override fun repeat(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		return executor.scheduleAtFixedRate(SafeCode(code), delay, delay, unit).asCancelable()
	}
}