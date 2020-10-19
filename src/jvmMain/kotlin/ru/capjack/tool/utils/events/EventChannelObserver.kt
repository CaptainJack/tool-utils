package ru.capjack.tool.utils.events

interface EventChannelObserver {
	fun observerReceiverAdded() {}
	
	fun observerReceiverRemoved() {}
	
	fun observerReceiverCleared() {}
}