package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

actual interface Executor {
	actual fun execute(fn: () -> Unit)
	
	actual fun submit(fn: () -> Unit): Cancelable
	
	fun execute(command: Runnable)
}