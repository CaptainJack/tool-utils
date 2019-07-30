package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Callable
import ru.capjack.tool.utils.Cancelable

actual interface Executor {
	actual fun execute(fn: () -> Unit)
	
	actual fun submit(fn: () -> Unit): Cancelable
	
	fun execute(task: Callable<Unit>)
	
	fun submit(task: Callable<Unit>): Cancelable
}
