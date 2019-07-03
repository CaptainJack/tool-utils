package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ExecutorService

internal open class ExecutorImpl<S : ExecutorService>(protected val service: S) : Executor {
	override fun execute(fn: () -> Unit) {
		service.execute(fn)
	}
	
	override fun execute(command: Runnable) {
		service.execute(command)
	}
	
	override fun submit(fn: () -> Unit): Cancelable {
		return service.submit(fn).asCancelable()
	}
}

