package ru.capjack.tool.utils.keeper

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction

abstract class AbstractKeeper<I : Any, E : Any>(
	private val assistant: TemporalAssistant,
	private val lifetime: Long,
	private val lifetimeUnit: TimeUnit
) : Keeper<I, E> {
	
	private val entities = ConcurrentHashMap<I, Entry<E>>()
	
	init {
		require(lifetime > 0)
	}
	
	protected abstract fun load(id: I): E
	
	protected abstract fun flush(id: I, entity: E)
	
	override fun touch(id: I): E? {
		return entities.computeIfPresent(id, touchMapper)?.entity
	}
	
	private val touchMapper = BiFunction { id: I, entry: Entry<E>? ->
		entry?.also {
			if (it.holds == 0) scheduleKill(id, it)
		}
	}
	
	override fun get(id: I): E {
		return entities.compute(id, getMapper)?.entity ?: throw RuntimeException("Fail to get entity (id: $id)")
	}
	
	private val getMapper = BiFunction { id: I, entry: Entry<E>? ->
		entry?.also {
			if (it.holds == 0) scheduleKill(id, it)
		} ?: try {
			Entry(load(id)).also { scheduleKill(id, it) }
		}
		catch (e: Throwable) {
			ownLogger.error("Fail to load entity (id: $id)", e)
			null
		}
	}
	
	final override fun hold(id: I): E {
		return entities.compute(id, holdMapper)?.entity ?: throw RuntimeException("Fail to hold entity (id: $id)")
	}
	
	private val holdMapper = BiFunction { id: I, entry: Entry<E>? ->
		entry?.also {
			if (++it.holds == 1) {
				it.killer.cancel()
				it.killer = Cancelable.DUMMY
			}
		} ?: try {
			Entry(load(id)).also { it.holds = 1 }
		}
		catch (e: Throwable) {
			ownLogger.error("Fail to load entity (id: $id)", e)
			null
		}
	}
	
	final override fun release(id: I) {
		entities.compute(id, releaseMapper)
	}
	
	private val releaseMapper = BiFunction { id: I, entry: Entry<E>? ->
		if (entry == null) {
			ownLogger.error("Entity is not held (id: $id)")
		}
		else {
			val holds = --entry.holds
			if (holds == 0) {
				scheduleKill(id, entry)
			}
			else if (holds < 0) {
				ownLogger.warn("Excess entity releases (id: $id)")
			}
		}
		entry
	}
	
	fun flush(): Boolean {
		entities.keys.toList().forEach {
			entities.computeIfPresent(it, flushMapper)
		}
		return entities.isEmpty()
	}
	
	private val flushMapper = BiFunction { id: I, entry: Entry<E> ->
		try {
			flush(id, entry.entity)
		}
		catch (e: Throwable) {
			ownLogger.error("Fail to flush entity (id: $id)", e)
		}
		
		if (entry.holds == 0) {
			entry.killer = Cancelable.DUMMY
			null
		}
		else entry
	}
	
	private fun kill(id: I) {
		entities.compute(id, killMapper)
	}
	
	private val killMapper = BiFunction { id: I, entry: Entry<E>? ->
		if (entry == null) {
			ownLogger.error("Kill failed, the entity is missing (id: $id)")
			null
		}
		else flushMapper.apply(id, entry)
	}
	
	private fun scheduleKill(id: I, entry: Entry<E>) {
		entry.killer.cancel()
		entry.killer = assistant.schedule(lifetime, lifetimeUnit) { kill(id) }
	}
	
	private class Entry<E : Any>(val entity: E) {
		@Volatile
		var holds = 0
		
		@Volatile
		var killer = Cancelable.DUMMY
	}
}