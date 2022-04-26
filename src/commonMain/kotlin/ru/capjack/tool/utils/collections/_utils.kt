package ru.capjack.tool.utils.collections

fun Collection<*>.checkIndex(index: Int) {
	if (index < 0 || index >= size) {
		throw IndexOutOfBoundsException("index: $index, size: $size")
	}
}

inline fun <T, R> Array<out T>.letFirstNotNull(transform: (T) -> R?): R? {
	for (e in this) {
		val r = transform(e)
		if (r != null) return r
	}
	return null
}

inline fun <T, R> Iterable<T>.letFirstNotNull(transform: (T) -> R?): R? {
	for (e in this) {
		val r = transform(e)
		if (r != null) return r
	}
	return null
}

inline fun <reified T> MutableCollection<T>.clearAndEach(action: (T) -> Unit) {
	val array = toTypedArray()
	clear()
	array.forEach(action)
}