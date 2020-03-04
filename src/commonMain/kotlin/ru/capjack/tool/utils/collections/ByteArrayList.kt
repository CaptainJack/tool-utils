package ru.capjack.tool.utils.collections

open class ByteArrayList(private val array: ByteArray) : AbstractPrimitiveArrayList<Byte>() {
	
	constructor(size: Int) : this(ByteArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Byte): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Byte): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Byte): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Byte> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Byte {
		return array[index]
	}
	
	override fun set0(index: Int, element: Byte) {
		array[index] = element
	}
}