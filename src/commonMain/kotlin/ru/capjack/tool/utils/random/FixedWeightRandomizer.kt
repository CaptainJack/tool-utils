package ru.capjack.tool.utils.random

class FixedWeightRandomizer(
	private var weights: IntArray
) : WeightRandomizer() {
	
	constructor(weights: List<Int>) : this(weights.toIntArray())
	
	override var totalWeight = weights.sum()
	override fun getWeight(index: Int) = weights[index]
}


