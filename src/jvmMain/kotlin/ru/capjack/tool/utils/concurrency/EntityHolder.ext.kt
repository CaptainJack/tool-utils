package ru.capjack.tool.utils.concurrency

inline fun <I : Any, E : Any, R> EntityHolder<I, E>.use(id: I, block: (E) -> R): R {
	val entity = hold(id)
	try {
		return block(entity)
	}
	finally {
		release(id)
	}
}