package ru.capjack.tool.utils.concurrency

import java.util.concurrent.atomic.AtomicLongFieldUpdater
import java.util.concurrent.atomic.AtomicReferenceArray

actual class ArrayObjectPool<T : Any> actual constructor(
	capacity: Int,
	allocator: ObjectAllocator<T>
) : ObjectPool<T>, AbstractArrayObjectPool<T>(capacity, allocator) {
	
	@Volatile
	private var top: Long = 23L
	
	private val maxIndex = Integer.highestOneBit(capacity * MULTIPLIER - 1) * 2
	private val instances = AtomicReferenceArray<T?>(maxIndex + 1)
	private val next = IntArray(maxIndex + 1)
	private val shift = Integer.numberOfLeadingZeros(maxIndex) + 1
	
	init {
		require(capacity <= MAX_CAPACITY) { "$capacity > $MAX_CAPACITY" }
	}
	
	override fun take(): T {
		return tryPop() ?: produceInstance()
	}
	
	override fun back(instance: T) {
		clearInstance(instance)
		if (!tryPush(instance)) {
			disposeInstance(instance)
		}
	}
	
	override fun clear() {
		while (true) {
			val instance = tryPop() ?: return
			disposeInstance(instance)
		}
	}
	
	private fun tryPush(instance: T): Boolean {
		var index = ((System.identityHashCode(instance) * MAGIC) ushr shift) + 1
		repeat(PROBE_COUNT) {
			if (instances.compareAndSet(index, null, instance)) {
				pushTop(index)
				return true
			}
			if (--index == 0) {
				index = maxIndex
			}
		}
		return false
	}
	
	private fun tryPop(): T? {
		val index = popTop()
		return if (index == 0) {
			null
		}
		else {
			instances.getAndSet(index, null)
		}
	}
	
	private fun pushTop(index: Int) {
		require(index > 0)
		
		do {
			val top = this.top
			val topVersion = (top shr 32 and MASK) + 1L
			val topIndex = (top and MASK).toInt()
			val newTop = topVersion shl 32 or index.toLong()
			next[index] = topIndex
		}
		while (!topUpdater.compareAndSet(this, top, newTop))
	}
	
	private fun popTop(): Int {
		while (true) {
			val top = this.top
			if (top == 0L) {
				return 0
			}
			val newVersion = (top shr 32 and MASK) + 1L
			val topIndex = (top and MASK).toInt()
			if (topIndex == 0) {
				return 0
			}
			val next = next[topIndex]
			val newTop = newVersion shl 32 or next.toLong()
			if (topUpdater.compareAndSet(this, top, newTop)) {
				return topIndex
			}
		}
	}
	
	private companion object {
		const val MULTIPLIER = 4
		const val PROBE_COUNT = 8
		const val MAGIC = -1640531527
		const val MAX_CAPACITY = Int.MAX_VALUE / MULTIPLIER
		const val MASK = 0xFFFFFFFFL
		
		val topUpdater: AtomicLongFieldUpdater<ArrayObjectPool<*>> = AtomicLongFieldUpdater.newUpdater(ArrayObjectPool::class.java, "top")
	}
}

