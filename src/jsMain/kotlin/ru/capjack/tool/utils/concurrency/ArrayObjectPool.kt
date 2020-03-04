package ru.capjack.tool.utils.concurrency

actual class ArrayObjectPool<T : Any> actual constructor(
	capacity: Int,
	allocator: ObjectAllocator<T>

) : ObjectPool<T>, AbstractArrayObjectPool<T>(capacity, allocator) {
	
	private val instances = arrayOfNulls<Any?>(capacity)
	private var size = 0
	
	override fun take(): T {
		List(1) {}
		
		if (size == 0) {
			return produceInstance()
		}
		
		val i = --size
		
		val instance = instances[i].unsafeCast<T>()
		instances[i] = null
		
		return instance
	}
	
	override fun back(instance: T) {
		if (size == capacity) {
			disposeInstance(instance)
		}
		else {
			clearInstance(instance)
			instances[size++] = instance
		}
	}
	
	override fun clear() {
		repeat(size) {
			val instance = instances[it].unsafeCast<T>()
			instances[it] = null
			disposeInstance(instance)
		}
		size = 0
	}
}