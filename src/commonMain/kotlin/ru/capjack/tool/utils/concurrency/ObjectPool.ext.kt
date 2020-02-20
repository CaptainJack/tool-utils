package ru.capjack.tool.utils.concurrency

inline fun <T : Any, R> ObjectPool<T>.use(code: (T) -> R): R {
	val instance = take()
	try {
		return code(instance)
	} finally {
		back(instance)
	}
}