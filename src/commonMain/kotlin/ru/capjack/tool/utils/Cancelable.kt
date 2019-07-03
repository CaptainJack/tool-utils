package ru.capjack.tool.utils

interface Cancelable {
	fun cancel()
	
	companion object {
		val FAKE = object : Cancelable {
			override fun cancel() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Cancelable {
			return object : Cancelable {
				override fun cancel() = block.invoke()
			}
		}
	}
}