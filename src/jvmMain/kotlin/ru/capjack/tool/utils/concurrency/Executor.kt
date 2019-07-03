package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ExecutorService

actual interface Executor {
	actual fun execute(fn: () -> Unit)
	
	actual fun submit(fn: () -> Unit): Cancelable
	
	fun execute(command: Runnable)
	
	companion object {
		operator fun invoke(service: ExecutorService): Executor {
			return ExecutorImpl(service)
		}
	}
}