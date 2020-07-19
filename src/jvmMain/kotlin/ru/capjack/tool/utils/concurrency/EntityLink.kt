package ru.capjack.tool.utils.concurrency

interface EntityLink<E : Any> {
	val entity: E
	
	fun release()
}