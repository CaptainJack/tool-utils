package ru.capjack.tool.utils.keeper

interface Keeper<I : Any, out E : Any> {
	fun get(id: I): E
	
	fun hold(id: I): E
	
	fun release(id: I)
	
	fun holdLink(id: I): Link<E> = LinkImpl(this, id, hold(id))
}

inline fun <I : Any, E : Any, R> Keeper<I, E>.use(id: I, block: (E) -> R): R {
	val entity = hold(id)
	try {
		return block(entity)
	}
	finally {
		release(id)
	}
}