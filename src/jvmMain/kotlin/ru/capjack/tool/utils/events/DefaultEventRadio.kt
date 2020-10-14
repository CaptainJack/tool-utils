package ru.capjack.tool.utils.events

import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.assistant.Assistant
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass

open class DefaultEventRadio<E : Any>(type: KClass<E>, private val assistant: Assistant) : EventRadio<E> {
	
	private val main = DefaultClearableEventChannel(type, assistant)
	private val wavesLock = ReentrantReadWriteLock()
	private val waves = ArrayList<Wave<out E, *>>()
	
	override fun <T : E, K : Any> registerWave(type: KClass<T>, keyer: (T) -> K): EventRadio.Wave<T, K> {
		val wave = Wave(type, keyer)
		wavesLock.write {
			waves.add(wave)
		}
		return wave
	}
	
	override fun dispatchEvent(event: E) {
		val type = event::class
		main.dispatchEvent(event)
		wavesLock.read {
			waves.forEach {
				if (it.match(type)) {
					it.dispatchEvent(event)
				}
			}
		}
	}
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return main.receiveEvent(type, receiver)
	}
	
	override fun isEmpty(): Boolean {
		return main.isEmpty() && wavesLock.read { waves.all { it.isEmpty() } }
	}
	
	private inner class Wave<T : E, K : Any>(type: KClass<T>, private val keyer: (T) -> K) : EventRadio.Wave<T, K> {
		private val emerger = WaveEmerger(type)
		
		private val channelsLock = ReentrantReadWriteLock()
		private val channels = HashMap<K, EventChannel<T>>()
		
		override fun forKey(key: K): EventDealer<T> {
			return object : EventDealer<T> {
				override fun <S : T> receiveEvent(type: KClass<S>, receiver: (S) -> Unit): Cancelable {
					return this@Wave.receiveEvent(key, type, receiver)
				}
			}
		}
		
		override fun <S : T> receiveEvent(key: K, type: KClass<S>, receiver: (S) -> Unit): Cancelable {
			channelsLock.write {
				val channel = channels.getOrPut(key) { DefaultClearableEventChannel(emerger, assistant) }
				val cancelable = channel.receiveEvent(type, receiver)
				
				return Cancelable {
					cancelable.cancel()
					if (channel.isEmpty()) {
						channelsLock.write {
							if (channel.isEmpty()) {
								channels.remove(key)
							}
						}
					}
				}
			}
		}
		
		fun match(type: KClass<*>): Boolean {
			return emerger.match(type)
		}
		
		fun dispatchEvent(event: E) {
			@Suppress("UNCHECKED_CAST")
			event as T
			val key = keyer(event)
			channelsLock.read { channels[key] }?.dispatchEvent(event)
		}
		
		fun isEmpty(): Boolean {
			return channelsLock.read { channels.values.all { it.isEmpty() } }
		}
	}
	
	private class WaveEmerger<E : Any>(type: KClass<E>) : AutoEventTypeEmerger<E>(type) {
		
		private val empty = emptyArray<KClass<out E>>()
		
		override fun convertTypes(list: MutableList<KClass<out E>>): Array<KClass<out E>> {
			return if (list.isEmpty()) empty else super.convertTypes(list)
		}
	}
}