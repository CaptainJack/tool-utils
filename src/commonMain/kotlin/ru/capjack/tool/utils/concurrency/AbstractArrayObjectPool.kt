package ru.capjack.tool.utils.concurrency

abstract class AbstractArrayObjectPool<T : Any>(allocator: ObjectAllocator<T>, protected val capacity: Int) : AbstractObjectPool<T>(allocator) {
	init {
		require(capacity > 0)
	}
}