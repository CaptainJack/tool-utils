package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.ConcurrentHashMap

abstract class InternalEntityHolder<I : Any, E : InternalEntity>(
	private val assistant: DelayableAssistant,
	lifetimeSeconds: Int
) : EntityHolder<I, E> {
	
	private val entities = ConcurrentHashMap<I, E>()
	private val tryKillTimeout: Int = lifetimeSeconds * 1000 / 2
	
	init {
		require(lifetimeSeconds > 0)
	}
	
	override fun get(id: I): E {
		return touch(id, fetch(id))
	}
	
	override fun hold(id: I): E {
		return hold(id, fetch(id))
	}
	
	override fun release(id: I) {
		val entity = fetch(id)
		val lock = entity.lock
		
		synchronized(lock) {
			var holds = lock.holds
			if (holds <= 0) {
				ownLogger.error("Excess entity releases (id: $id, ${entity::class})")
			}
			else {
				holds -= 1
				lock.holds = holds
				if (holds == 0) {
					lock.touched = true
					scheduleTryKill(id, entity)
				}
			}
		}
	}
	
	fun clear() {
		while (entities.isNotEmpty()) {
			entities.toList().forEach {
				kill(it.first, it.second)
			}
		}
	}
	
	protected abstract fun load(id: I): E
	
	protected abstract fun save(id: I, entity: E)
	
	protected abstract fun free(id: I, entity: E)
	
	private fun fetch(id: I): E {
		return entities.getOrPut(id) { load(id) }
	}
	
	private fun touch(id: I, entity: E): E {
		val other: E?
		val lock = entity.lock
		
		synchronized(lock) {
			val holds = lock.holds
			if (holds == -1) {
				other = entities.putIfAbsent(id, entity)
				if (other == null) {
					lock.holds = 0
					lock.touched = true
					scheduleTryKill(id, entity)
				}
			}
			else {
				other = null
				if (holds == 0 && !lock.touched) {
					lock.touched = true
					scheduleTryKill(id, entity)
				}
			}
		}
		
		return if (other == null) entity else touch(id, other)
	}
	
	private fun hold(id: I, entity: E): E {
		val other: E?
		val lock = entity.lock
		
		synchronized(lock) {
			val holds = lock.holds
			if (holds == -1) {
				other = entities.putIfAbsent(id, entity)
				if (other == null) {
					lock.holds = 1
				}
			}
			else {
				other = null
				if (holds == 0) {
					cancelTryKill(entity)
					lock.touched = false
				}
				lock.holds = holds + 1
			}
		}
		
		return if (other == null) entity else hold(id, other)
	}
	
	private fun scheduleTryKill(id: I, entity: E) {
		entity.lock.killer = assistant.schedule(tryKillTimeout) { tryKill(id, entity) }
	}
	
	private fun cancelTryKill(entity: E) {
		entity.lock.apply {
			killer.cancel()
			killer = Cancelable.DUMMY
		}
	}
	
	private fun tryKill(id: I, entity: E) {
		val lock = entity.lock
		
		synchronized(lock) {
			if (lock.touched) {
				lock.touched = false
				scheduleTryKill(id, entity)
			}
			else if (lock.holds == 0) {
				kill(id, entity)
			}
		}
	}
	
	private fun kill(id: I, entity: E) {
		val lock = entity.lock
		
		synchronized(lock) {
			if (lock.holds != 0) {
				ownLogger.warn("Entity $entity ($id) not released")
			}
			
			lock.holds = -1
			lock.touched = false
			
			try {
				save(id, entity)
			}
			catch (e: Throwable) {
				ownLogger.error("Fail to save entity '$entity'", e)
			}
			
			try {
				free(id, entity)
			}
			catch (e: Throwable) {
				ownLogger.error("Fail to free entity '$entity'", e)
			}
			
			entities.remove(id).also {
				if (it !== entity) ownLogger.error("The forgotten user does not match the required one")
			}
		}
	}
}