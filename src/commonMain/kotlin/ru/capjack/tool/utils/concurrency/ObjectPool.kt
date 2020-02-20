package ru.capjack.tool.utils.concurrency

interface ObjectPool<T: Any> {
	fun take(): T
	
	fun back(instance: T)
	
	fun clear()
}
