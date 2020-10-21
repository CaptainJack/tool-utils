package ru.capjack.tool.utils.keeper

import ru.capjack.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.TimeUnit

class StorageKeeper<I : Any, E : Any>(
	assistant: TemporalAssistant,
	lifetime: Long,
	lifetimeUnit: TimeUnit,
	private val storage: Storage<I, E>
) : AbstractKeeper<I, E>(assistant, lifetime, lifetimeUnit) {
	
	override fun load(id: I) = storage.loadEntity(id)
	
	override fun flush(id: I, entity: E) = storage.flushEntity(id, entity)
}