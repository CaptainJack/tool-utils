package ru.capjack.tool.utils

interface Closeable {
	fun close()
	
	companion object {
		val FAKE = object : Closeable {
			override fun close() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Closeable {
			return object : Closeable {
				override fun close() = block.invoke()
			}
		}
	}
}