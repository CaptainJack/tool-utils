package ru.capjack.tool.utils.keeper

import ru.capjack.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.TimeUnit

class StorageKeeper<I : Any, E : Any, M : E>(
	assistant: TemporalAssistant,
	lifetime: Long,
	lifetimeUnit: TimeUnit,
	private val storage: Storage<I, E, M>
) : AbstractKeeper<I, E, M>(assistant, lifetime, lifetimeUnit) {
	
	override fun load(id: I) = storage.loadEntity(id)
	
	override fun save(id: I, entity: M) = storage.saveEntity(id, entity)
	
	override fun kill(id: I, entity: E)  = storage.killEntity(id, entity)
}