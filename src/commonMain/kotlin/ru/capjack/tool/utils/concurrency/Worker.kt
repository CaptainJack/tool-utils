package ru.capjack.tool.utils.concurrency

expect open class Worker(executor: Executor) {
	val accessible: Boolean
	
	fun execute(task: () -> Unit)
	
	fun defer(task: () -> Unit)
	
	fun capture(): Boolean
	
	fun release()
}