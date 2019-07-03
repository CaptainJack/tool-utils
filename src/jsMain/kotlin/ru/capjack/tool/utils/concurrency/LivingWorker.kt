package ru.capjack.tool.utils.concurrency

actual class LivingWorker actual constructor(executor: Executor) : Worker(executor) {
	actual val alive: Boolean
		get() = TODO("not implemented")
	
	actual fun die() {
	}
	
}