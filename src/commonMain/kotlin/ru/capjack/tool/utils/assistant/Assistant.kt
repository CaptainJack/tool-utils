package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable

interface Assistant {
	fun execute(code: () -> Unit)
	
	fun charge(code: () -> Unit): Cancelable
}

