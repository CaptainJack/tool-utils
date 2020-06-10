package ru.capjack.tool.utils

interface Stoppable {
	fun stop()
	
	companion object {
		val DUMMY = object : Stoppable {
			override fun stop() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Stoppable {
			return object : Stoppable {
				override fun stop() = block.invoke()
			}
		}
	}
}

class CompositeStoppable(private vararg val targets: Stoppable) : Stoppable {
	constructor(targets: List<Stoppable>): this(*targets.toTypedArray())
	
	override fun stop() {
		targets.forEach(Stoppable::stop)
	}
}