package ru.capjack.tool.utils.random

import ru.capjack.tool.utils.Supplier

open class DynamicWeightRandomizer(
	private val weights: List<Supplier<Int>>
) : WeightRandomizer() {
	
	override var totalWeight: Int = 0
	
	init {
		update()
	}
	
	override fun getWeight(index: Int) = weights[index].get()
	
	fun update() {
		totalWeight = weights.sumOf { it.get() }
	}
}