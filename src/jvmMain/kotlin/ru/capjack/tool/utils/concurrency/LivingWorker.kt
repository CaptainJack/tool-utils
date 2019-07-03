package ru.capjack.tool.utils.concurrency

actual class LivingWorker actual constructor(executor: Executor) : Worker(executor) {
	
	@Volatile
	private var _alive = true
	
	actual val alive
		get() = _alive
	
	actual fun die() {
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