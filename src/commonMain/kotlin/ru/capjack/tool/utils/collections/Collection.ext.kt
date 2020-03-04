package ru.capjack.tool.utils.collections

fun Collection<*>.checkIndex(index: Int) {
	if (index < 0 || index >= size) {
		throw IndexOutOfBoundsException("index: $index, size: $size")
	}
}