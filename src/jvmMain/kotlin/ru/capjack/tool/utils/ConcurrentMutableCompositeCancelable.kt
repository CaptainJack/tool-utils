package ru.capjack.tool.utils

import ru.capjack.tool.utils.collections.clearAndEach
import java.util.concurrent.ConcurrentHashMap

class ConcurrentMutableCompositeCancelable() : Cancelable {
	private val targets = ConcurrentHashMap.newKeySet<Cancelable>()
	
	override fun cancel() {
		targets.clearAndEach(Cancelable::cancel)
	}
	
	fun add(target: Cancelable): Cancelable {
		targets.add(target)
		return Cancelable {
			remove(target)
			target.cancel()
		}
	}
	
	fun remove(target: Cancelable) {
		targets.remove(target)
	}
}