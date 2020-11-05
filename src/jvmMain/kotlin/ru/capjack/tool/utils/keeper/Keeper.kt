package ru.capjack.tool.utils.keeper

interface Keeper<I : Any, out E : Any> {
	fun touch(id: I): E?
	
	fun get(id: I): E
	
	fun hold(id: I): E
	
	fun release(id: I)
}

inline fun <I : Any, E : Any, R> Keeper<I, E>.capture(id: I, block: (E) -> R): R {
	val entity = hold(id)
	try {
		return block(entity)
	}
	finally {
		release(id)
	}
}

fun <I : Any, E : Any> Keeper<I, E>.holdLink(id: I): Link<E> {
	return LinkImpl(this, id, hold(id))
}