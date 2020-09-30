package ru.capjack.tool.utils.events

interface ClearableEventChannel<E : Any> : EventChannel<E> {
	fun clearEventObservers()
}