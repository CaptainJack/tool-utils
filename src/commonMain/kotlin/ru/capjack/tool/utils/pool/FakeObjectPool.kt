package ru.capjack.tool.utils.pool

class FakeObjectPool<T : Any>(private val allocator: () -> T) : ObjectPool<T> {
	override fun take(): T {
		return allocator.invoke()
	}
	
	override fun back(instance: T) {}
	
	override fun clear() {}
}