package ru.capjack.tool.utils.events

object DummyEventChannelObserver : EventChannelObserver {
	override fun observerReceiverAdded() {}
	
	override fun observerReceiverRemoved() {}
	
	override fun observerReceiverCleared() {}
}