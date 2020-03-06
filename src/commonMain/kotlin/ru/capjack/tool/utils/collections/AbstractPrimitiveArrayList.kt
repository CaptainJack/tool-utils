package ru.capjack.tool.utils.collections

abstract class AbstractPrimitiveArrayList<E, A>(protected val array: A) : AbstractList<E>(), PrimitiveArrayList<E>, RandomAccess {
	override fun get(index: Int): E {
		checkIndex(index)
		return get0(index)
	}
	
	override fun set(index: Int, element: E): E {
		checkIndex(index)
		val old = get0(index)
		set0(index, element)
		return old
	}
	
	protected abstract fun get0(index: Int): E
	
	protected abstract fun set0(index: Int, element: E)
	
	override fun <T> getSourceArray(): T {
		@Suppress("UNCHECKED_CAST")
		return array as T
	}
}