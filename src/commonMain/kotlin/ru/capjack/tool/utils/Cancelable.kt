package ru.capjack.tool.utils

import kotlin.jvm.Volatile

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

class MediatorCancelable(
	@Volatile private var target: Cancelable? = null
) : Cancelable {
	override fun cancel() {
		target?.cancel()
		target = null
	}
	
	fun assign(target: Cancelable) {
		this.target = target
	}
	
	fun release() {
		target = null
	}
}

class CompositeCancelable(targets: Collection<Cancelable>) : Cancelable {
	private val targets = targets.toList()
	
	constructor(vararg targets: Cancelable) : this(targets.toList())
	
	override fun cancel() {
		targets.forEach(Cancelable::cancel)
	}
}

class MutableCompositeCancelable(targets: Collection<Cancelable>) : Cancelable {
	private val targets = targets.toHashSet()
	
	constructor(vararg targets: Cancelable): this(targets.toList())
	
	override fun cancel() {
		targets.forEach(Cancelable::cancel)
	}
	
	fun add(target: Cancelable) {
		targets.add(target)
	}
	fun remove(target: Cancelable) {
		targets.remove(target)
	}
}
