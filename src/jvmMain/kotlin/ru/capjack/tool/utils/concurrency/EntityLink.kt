package ru.capjack.tool.utils.concurrency

interface EntityLink<out E : Any> {
	val entity: E
	
	fun release()
}