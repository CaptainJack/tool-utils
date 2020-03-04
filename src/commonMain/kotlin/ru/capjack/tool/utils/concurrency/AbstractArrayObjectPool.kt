package ru.capjack.tool.utils.concurrency

abstract class AbstractArrayObjectPool<T : Any>(protected val capacity: Int, allocator: ObjectAllocator<T>) : AbstractObjectPool<T>(allocator) {
	init {
		require(capacity > 0)
	}
}