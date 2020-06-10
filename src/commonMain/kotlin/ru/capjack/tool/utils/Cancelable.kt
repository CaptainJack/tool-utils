package ru.capjack.tool.utils

interface Cancelable {
	fun cancel()
	
	companion object {
		val DUMMY = object : Cancelable {
			override fun cancel() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Cancelable {
			return object : Cancelable {
				override fun cancel() = block.invoke()
			}
		}
	}
}

class CompositeCancelable(private vararg val targets: Cancelable) : Cancelable {
	constructor(targets: List<Cancelable>) : this(*targets.toTypedArray())
	
	override fun cancel() {
		targets.forEach(Cancelable::cancel)
	}
}