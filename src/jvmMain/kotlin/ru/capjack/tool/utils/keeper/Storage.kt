package ru.capjack.tool.utils.keeper

interface Storage<I : Any, E : Any> {
	fun loadEntity(id: I): E
	
	fun flushEntity(id: I, entity: E)
}