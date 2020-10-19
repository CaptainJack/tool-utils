package ru.capjack.tool.utils.keeper

class LinkImpl<I : Any, E : Any>(private val keeper: Keeper<I, E>, private val id: I, entity: E) : Link<E> {
	
	override val entity = entity
		get() = if (alive) field else throw IllegalStateException()
	
	@Volatile
	private var alive = true
	
	override fun release() {
		synchronized(this) {
			check(alive)
			alive = false
		}
		keeper.release(id)
	}
	
}