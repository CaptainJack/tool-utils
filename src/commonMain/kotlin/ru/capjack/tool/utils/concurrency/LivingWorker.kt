package ru.capjack.tool.utils.concurrency

import kotlin.jvm.Volatile

class LivingWorker(assistant: Assistant, errorHandler: (Throwable) -> Unit) : Worker(assistant, errorHandler) {
	@Volatile
	private var _alive = true
	
	val alive
		get() = _alive
	
	fun die() {
		if (_alive) {
			if (accessible) {
				_alive = false
			}
			else {
				execute(::die)
			}
		}
	}
}
