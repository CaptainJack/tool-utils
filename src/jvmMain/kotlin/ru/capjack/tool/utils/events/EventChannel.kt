package ru.capjack.tool.utils.events

interface EventChannel<E : Any> : EventDispatcher<E>, EventDealer<E> {
	fun isEmpty(): Boolean
}