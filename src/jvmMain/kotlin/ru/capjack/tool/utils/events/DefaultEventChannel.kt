package ru.capjack.tool.utils.events

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.ErrorHandler
import ru.capjack.tool.utils.assistant.Assistant
import ru.capjack.tool.utils.worker.Worker
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass

class DefaultEventChannel<E : Any>(
	assistant: Assistant,
	private val emerger: EventTypeEmerger<E>,
	private val observer: EventChannelObserver
) : ClearableEventChannel<E>, ErrorHandler {
	
	private val worker = Worker(assistant, this)
	private val receiversMap = ConcurrentHashMap<KClass<out E>, Receivers>()
	
	constructor(type: KClass<E>, assistant: Assistant) : this(assistant, AutoEventTypeEmerger(type), DummyEventChannelObserver)
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return receiversMap.getOrPut(type, this::Receivers).add(receiver)
	}
	
	override fun dispatchEvent(event: E) {
		worker.defer {
			emerger.emerge(event::class).forEach {
				receiversMap[it]?.dispatch(event)
			}
		}
	}
	
	override fun clearEventReceivers() {
		receiversMap.values.forEach { it.clear() }
		observer.observerReceiverCleared()
	}
	
	override fun handleError(error: Throwable) {
		ownLogger.error("Error when dispatching events", error)
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
			receivers.forEach {
				worker.protect { it(event) }
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