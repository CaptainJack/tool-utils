package ru.capjack.tool.utils.collections

open class DoubleArrayList(private val array: DoubleArray) : AbstractPrimitiveArrayList<Double>() {
	
	constructor(size: Int) : this(DoubleArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Double): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Double): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Double): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Double> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Double {
		return array[index]
	}
	
	override fun set0(index: Int, element: Double) {
		array[index] = element
	}
}