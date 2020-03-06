package ru.capjack.tool.utils.collections

open class LongArrayList(array: LongArray) : AbstractPrimitiveArrayList<Long, LongArray>(array) {
	constructor(size: Int) : this(LongArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Long): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Long): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Long): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Long> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Long {
		return array[index]
	}
	
	override fun set0(index: Int, element: Long) {
		array[index] = element
	}
}