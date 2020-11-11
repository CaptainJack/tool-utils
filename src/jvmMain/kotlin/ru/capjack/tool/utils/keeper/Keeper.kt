package ru.capjack.tool.utils.keeper

interface Keeper<I : Any, out E : Any, out M : E> {
	fun touch(id: I): E?
	
	fun get(id: I): E
	
	fun hold(id: I): M
	
	fun release(id: I)
}

inline fun <I : Any, E : Any, R, M : E> Keeper<I, E, M>.capture(id: I, block: (M) -> R): R {
	val entity = hold(id)
	try {
		return block(entity)
	}
	finally {
		release(id)
	}
}

fun <I : Any, E : Any, M : E> Keeper<I, E, M>.holdLink(id: I): Link<M> {
	return LinkImpl(this, id, hold(id))
}