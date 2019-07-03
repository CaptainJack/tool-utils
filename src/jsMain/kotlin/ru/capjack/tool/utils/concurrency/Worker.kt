package ru.capjack.tool.utils.concurrency

actual open class Worker actual constructor(executor: Executor) {
	actual val accessible: Boolean
		get() = TODO("not implemented")
	
	actual fun execute(task: () -> Unit) {
	}
	
	actual fun defer(task: () -> Unit) {
	}
	
	actual fun capture(): Boolean {
		TODO("not implemented")
	}
	
	actual fun release() {
	}
	
}