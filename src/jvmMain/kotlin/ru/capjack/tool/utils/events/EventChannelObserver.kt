package ru.capjack.tool.utils.events

interface EventChannelObserver {
	fun observeReceiverAdded()
	
	fun observeReceiverRemoved()
}