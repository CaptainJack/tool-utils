package ru.capjack.tool.utils.events

object DummyEventChannelObserver : EventChannelObserver {
	override fun observeReceiverAdded() {}
	
	override fun observeReceiverRemoved() {}
}