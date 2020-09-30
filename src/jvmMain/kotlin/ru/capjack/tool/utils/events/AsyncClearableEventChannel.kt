package ru.capjack.tool.utils.events

import ru.capjack.tool.logging.ownLogger
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.assistant.Assistant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass

class AsyncClearableEventChannel<E : Any>(private val assistant: Assistant) : ClearableEventChannel<E> {
	private val observersMap = ConcurrentHashMap<KClass<out E>, Observers>()
	
	override fun <T : E> observeEvent(type: KClass<T>, observer: (T) -> Unit): Cancelable {
		return observersMap.getOrPut(type) { Observers(assistant) }.add(observer)
	}
	
	override fun dispatchEvent(event: E) {
		observersMap[event::class]?.dispatch(event)
	}
	
	override fun clearEventObservers() {
		observersMap.values.forEach { it.clear() }
	}
	
	private class Observers(private val assistant: Assistant) {
		private val lock = ReentrantReadWriteLock()
		private val list = mutableSetOf<(Any) -> Unit>()
		
		fun <T : Any> add(observer: (T) -> Unit): Cancelable {
			lock.write {
				@Suppress("UNCHECKED_CAST")
				list.add(observer as (Any) -> Unit)
			}
			return Cancelable { remove(observer) }
		}
		
		private fun <T : Any> remove(observer: (T) -> Unit) {
			lock.write {
				@Suppress("UNCHECKED_CAST")
				list.remove(observer as (Any) -> Unit)
			}
		}
		
		fun dispatch(event: Any) {
			assistant.charge {
				lock.read {
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
		}
		
		fun clear() {
			lock.write {
				list.clear()
			}
		}
	}
}