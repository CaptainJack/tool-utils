package ru.capjack.tool.utils.keeper

interface Link<out E : Any> {
	val entity: E
	
	fun release()
}