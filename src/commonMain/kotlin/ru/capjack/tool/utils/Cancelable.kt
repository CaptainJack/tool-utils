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
		
		fun composite(vararg targets: Cancelable): Cancelable {
			return object : Cancelable {
				override fun cancel() = targets.forEach(Cancelable::cancel)
			}
		}
	}
}
