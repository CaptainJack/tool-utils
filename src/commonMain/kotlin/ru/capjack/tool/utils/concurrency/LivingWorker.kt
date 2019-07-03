package ru.capjack.tool.utils.concurrency

expect class LivingWorker(executor: Executor) : Worker {
	val alive: Boolean
	
	fun die()
}

