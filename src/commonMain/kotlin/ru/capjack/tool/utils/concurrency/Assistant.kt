package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

interface Assistant {
	fun execute(code: () -> Unit)
	
	fun charge(code: () -> Unit): Cancelable
}

