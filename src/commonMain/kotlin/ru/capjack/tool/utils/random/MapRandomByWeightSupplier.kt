package ru.capjack.tool.utils.random

import ru.capjack.tool.utils.Supplier

class MapRandomByWeightSupplier<T>(pairs: Map<T, Int>) : Supplier<T> {
	val values: List<T>
	val weights: WeightRandomizer
	
	init {
		val list = pairs.entries.toList()
		values = list.map { it.key }
		weights = FixedWeightRandomizer(list.map { it.value })
	}
	
	override fun get(): T {
		return values[weights.nextIndex()]
	}
}