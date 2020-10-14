package ru.capjack.tool.utils.events

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.assistant.Assistant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KClass

open class DefaultClearableEventChannel<E : Any>(private val emerger: EventTypeEmerger<E>, private val assistant: Assistant) : ClearableEventChannel<E> {
	
	constructor(type: KClass<E>, assistant: Assistant) : this(AutoEventTypeEmerger(type), assistant)
	
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
	}
	
	
	override fun isEmpty(): Boolean {
		return receiversMap.values.all { it.isEmpty() }
	}
	
	private inner class Receivers {
		private val list = CopyOnWriteArraySet<(Any) -> Unit>()
		
		fun <T : Any> add(observer: (T) -> Unit): Cancelable {
			@Suppress("UNCHECKED_CAST")
			list.add(observer as (Any) -> Unit)
			return Cancelable { list.remove(observer) }
		}
		
		fun dispatch(event: Any) {
			assistant.execute {
				list.forEach {
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
			list.clear()
		}
		
		fun isEmpty(): Boolean {
			return list.isEmpty()
		}
	}
}