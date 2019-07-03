package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

expect interface Executor {
	fun execute(fn: () -> Unit)
	
	fun submit(fn: () -> Unit): Cancelable
}

