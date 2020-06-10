package ru.capjack.tool.utils

interface Closeable {
	fun close()
	
	companion object {
		val DUMMY = object : Closeable {
			override fun close() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Closeable {
			return object : Closeable {
				override fun close() = block.invoke()
			}
		}
	}
}

class CompositeCloseable(private vararg val targets: Closeable) : Closeable {
	constructor(targets: List<Closeable>) : this(*targets.toTypedArray())
	
	override fun close() {
		targets.forEach(Closeable::close)
	}
}