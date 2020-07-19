package ru.capjack.tool.utils.concurrency

interface EntityHolder<I : Any, E : Any> {
	fun get(id: I): E
	
	fun hold(id: I): E
	
	fun release(id: I)
	
	fun holdLink(id: I): EntityLink<E> = EntityLinkImpl(this, id, hold(id))
}