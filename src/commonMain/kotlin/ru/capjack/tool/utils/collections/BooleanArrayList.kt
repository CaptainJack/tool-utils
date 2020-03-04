package ru.capjack.tool.utils.collections

open class BooleanArrayList(private val array: BooleanArray) : AbstractPrimitiveArrayList<Boolean>() {
	constructor(size: Int) : this(BooleanArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Boolean): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Boolean): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Boolean): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Boolean> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Boolean {
		return array[index]
	}
	
	override fun set0(index: Int, element: Boolean) {
		array[index] = element
	}
}