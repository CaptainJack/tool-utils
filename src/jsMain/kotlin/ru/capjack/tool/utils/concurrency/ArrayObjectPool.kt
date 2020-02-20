package ru.capjack.tool.utils.concurrency

actual class ArrayObjectPool<T : Any> actual constructor(
	allocator: ObjectAllocator<T>,
	capacity: Int

) : ObjectPool<T>, AbstractArrayObjectPool<T>(allocator, capacity) {
	
	private val instances = arrayOfNulls<Any?>(capacity)
	private var size = 0
	
	override fun take(): T {
		if (size == -1) {
			return produceInstance()
		}
		
		val i = --size
		
		val instance = instances[i].unsafeCast<T>()
		instances[i] = null
		
		clearInstance(instance)
		
		return instance
	}
	
	override fun back(instance: T) {
		if (size == capacity) {
			disposeInstance(instance)
		}
		else {
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