package ru.capjack.tool.utils.keeper

interface Storage<I : Any, E : Any, M : E> {
	fun loadEntity(id: I): M
	
	fun saveEntity(id: I, entity: M)
	
	fun killEntity(id: I, entity: E)
}