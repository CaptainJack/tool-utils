package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ExecutorService

abstract class AbstractExecutorAssistant<E : ExecutorService>(
	protected val executor: E
) : Assistant {
	
	override fun execute(code: () -> Unit) {
		executor.execute(code)
	}
	
	override fun charge(code: () -> Unit): Cancelable {
		return executor.submit(code).asCancelable()
	}
}