package ru.capjack.tool.utils.concurrency

interface ObjectAllocator<T : Any> {
	fun produceInstance(): T
	
	fun clearInstance(instance: T)
	
	fun disposeInstance(instance: T)
}