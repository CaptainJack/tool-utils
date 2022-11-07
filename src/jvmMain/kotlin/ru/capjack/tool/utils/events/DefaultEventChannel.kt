package ru.capjack.tool.utils.events

import ru.capjack.tool.lang.EMPTY_FUNCTION_1
import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.ErrorHandler
import ru.capjack.tool.utils.assistant.Assistant
import ru.capjack.tool.utils.worker.Worker
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
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
		worker.defer {
			receiversMap.values.forEach { it.clear() }
		}
	}
	
	override fun handleError(error: Throwable) {
		ownLogger.error("Error when dispatching events", error)
	}
	
	private inner class Receivers {
		private val receivers = CopyOnWriteArrayList<Receiver>()
		
		fun <T : Any> add(receiver: (T) -> Unit): Cancelable {
			@Suppress("UNCHECKED_CAST")
			val r = Receiver(receiver as (Any) -> Unit, receivers, observer)
			receivers.add(r)
			observer.observeReceiverAdded()
			return r
		}
		
		fun dispatch(event: Any) {
			receivers.forEach {
				worker.protect { it.receive(event) }
			}
		}
		
		fun clear() {
			receivers.toList().forEach { it.cancel() }
		}
	}
	
	private class Receiver(
		@Volatile private var receiver: (Any) -> Unit,
		@Volatile private var receivers: MutableCollection<Receiver>,
		@Volatile private var observer: EventChannelObserver
	) : Cancelable {
		override fun cancel() {
			if (receivers.remove(this)) {
				observer.observeReceiverRemoved()
				receiver = EMPTY_FUNCTION_1
				receivers = Collections.emptySet()
				observer = DummyEventChannelObserver
			}
		}
		
		fun receive(event: Any) {
			receiver.invoke(event)
		}
	}
}