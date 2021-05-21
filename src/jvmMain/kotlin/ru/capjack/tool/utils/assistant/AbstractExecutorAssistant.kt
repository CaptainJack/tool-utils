package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.asCancelable
import java.util.concurrent.ExecutorService

abstract class AbstractExecutorAssistant<E : ExecutorService>(
	protected val executor: E
) : Assistant {
	
	override fun execute(code: () -> Unit) {
		if (executor.isShutdown) safeCode(code)
		else executor.execute(SafeCode(code))
	}
	
	override fun charge(code: () -> Unit): Cancelable {
		if (executor.isShutdown) {
			safeCode(code)
			return Cancelable.DUMMY
		}
		return executor.submit(SafeCode(code)).asCancelable()
	}
}