package ru.capjack.tool.utils.concurrency

class EntityLinkImpl<I : Any, E : Any>(private val holder: EntityHolder<I, E>, private val id: I, entity: E) : EntityLink<E> {
	
	override val entity = entity
		get() = if (alive) field else throw IllegalStateException()
	
	@Volatile
	private var alive = true
	
	override fun release() {
		synchronized(this) {
			check(alive)
			alive = false
		}
		holder.release(id)
	}
	
}