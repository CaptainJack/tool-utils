package ru.capjack.tool.utils.collections

open class IntArrayList(private val array: IntArray) : AbstractPrimitiveArrayList<Int>() {
	
	constructor(size: Int) : this(IntArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Int): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Int): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Int): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Int> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Int {
		return array[index]
	}
	
	override fun set0(index: Int, element: Int) {
		array[index] = element
	}
}