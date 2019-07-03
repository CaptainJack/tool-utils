package ru.capjack.tool.utils

interface Stoppable {
	fun stop()
	
	companion object {
		val FAKE = object : Stoppable {
			override fun stop() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Stoppable {
			return object : Stoppable {
				override fun stop() = block.invoke()
			}
		}
	}
}