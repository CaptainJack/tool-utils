package ru.capjack.tool.utils.random

import ru.capjack.tool.utils.Supplier

class RandomByWeightSupplier<T>(
	private val values: List<T>,
	private val weights: WeightRandomizer
) : Supplier<T> {
	
	constructor(values: List<T>, weights: IntArray) : this(values, FixedWeightRandomizer(weights))
	constructor(values: List<T>, weights: List<Int>) : this(values, FixedWeightRandomizer(weights))
	constructor(pairs: List<Pair<T, Int>>) : this(pairs.map { it.first }, pairs.map { it.second })
	
	override fun get(): T {
		return values[weights.nextIndex()]
	}
}

