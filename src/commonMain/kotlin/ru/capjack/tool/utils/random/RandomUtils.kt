package ru.capjack.tool.utils.random

import kotlin.random.Random

object RandomUtils {
	fun gamble(chance: Double): Boolean {
		return when (chance) {
			0.0  -> false
			1.0  -> true
			0.5  -> Random.nextBoolean()
			else -> Random.nextDouble() < chance
		}
	}
	
	fun ofRange(from: Int, to: Int): Int {
		return if (from == to) from else Random.nextInt(from, to + 1)
	}
}