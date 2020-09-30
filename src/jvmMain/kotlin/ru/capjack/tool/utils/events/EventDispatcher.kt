package ru.capjack.tool.utils.events

interface EventDispatcher<E : Any> {
	fun dispatchEvent(event: E)
}