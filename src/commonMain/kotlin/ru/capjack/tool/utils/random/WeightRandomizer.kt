package ru.capjack.tool.utils.random

import kotlin.random.Random

abstract class WeightRandomizer {
	protected abstract val totalWeight: Int
	protected abstract fun getWeight(index: Int): Int
	
	fun nextIndex(): Int {
		val r = Random.nextInt(totalWeight)
		var a = 0
		var i = 0
		
		while (true) {
			a += getWeight(i)
			if (a > r) {
				return i
			}
			++i
		}
	}
	
	fun nextWeight(): Int {
		return getWeight(nextIndex())
	}
}