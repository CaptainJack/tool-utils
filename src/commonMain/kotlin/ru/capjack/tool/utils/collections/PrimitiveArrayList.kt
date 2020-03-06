package ru.capjack.tool.utils.collections

interface PrimitiveArrayList<E> : List<E> {
	operator fun set(index: Int, element: E): E
	
	fun <T> getSourceArray(): T
}