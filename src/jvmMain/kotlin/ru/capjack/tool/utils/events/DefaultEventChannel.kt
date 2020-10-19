package ru.capjack.tool.utils.events

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.assistant.Assistant
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass

class DefaultEventChannel<E : Any>(
	private val assistant: Assistant,
	private val emerger: EventTypeEmerger<E>,
	private val observer: EventChannelObserver
) : ClearableEventChannel<E> {
	
	constructor(type: KClass<E>, assistant: Assistant) : this(assistant, AutoEventTypeEmerger(type), DummyEventChannelObserver)
	
	private val receiversMap = ConcurrentHashMap<KClass<out E>, Receivers>()
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return receiversMap.getOrPut(type, this::Receivers).add(receiver)
	}
	
	override fun dispatchEvent(event: E) {
		emerger.emerge(event::class).forEach {
			receiversMap[it]?.dispatch(event)
		}
	}
	
	override fun clearEventReceivers() {
		receiversMap.values.forEach { it.clear() }
		observer.observerReceiverCleared()
	}
	
	private inner class Receivers {
		private val receivers = CopyOnWriteArraySet<(Any) -> Unit>()
		
		fun <T : Any> add(receiver: (T) -> Unit): Cancelable {
			@Suppress("UNCHECKED_CAST")
			if (receivers.add(receiver as (Any) -> Unit)) {
				observer.observerReceiverAdded()
			}
			return ReceiverCanceler(receiver, receivers, observer)
		}
		
		fun dispatch(event: Any) {
			assistant.execute {
				receivers.forEach {
					try {
						it.invoke(event)
					}
					catch (e: Throwable) {
						ownLogger.error("Event observers error", e)
					}
				}
			}
		}
		
		fun clear() {
			receivers.clear()
		}
	}
	
	private class ReceiverCanceler(
		var receiver: Any,
		var receivers: MutableSet<*>,
		var observer: EventChannelObserver
	) : Cancelable {
		override fun cancel() {
			if (receivers.remove(receiver)) {
				observer.observerReceiverRemoved()
				receiver = Unit
				receivers = Collections.emptySet<Any>()
				observer = DummyEventChannelObserver
			}
		}
	}
}