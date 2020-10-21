package ru.capjack.tool.utils.events

import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.assistant.TemporalAssistant
import ru.capjack.tool.utils.keeper.Keeper
import ru.capjack.tool.utils.keeper.Storage
import ru.capjack.tool.utils.keeper.StorageKeeper
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

open class DefaultEventRadio<E : Any>(
	type: KClass<E>,
	private val assistant: TemporalAssistant,
	private val waveDealerLifetime: Long,
	private val waveDealerLifetimeUnit: TimeUnit
) : EventRadio<E> {
	
	private val main = DefaultEventChannel(type, assistant)
	
	@Volatile
	private var waves = CopyOnWriteArrayList<Wave<out E, *>>()
	
	override fun <T : E, K : Any> registerWave(type: KClass<T>, keyer: (T) -> K): EventRadio.Wave<T, K> {
		val wave = Wave(type, keyer, assistant, waveDealerLifetime, waveDealerLifetimeUnit)
		waves.add(wave)
		return wave
	}
	
	override fun dispatchEvent(event: E) {
		val type = event::class
		main.dispatchEvent(event)
		
		waves.forEach {
			if (it.match(type)) {
				it.dispatchEvent(event)
			}
		}
	}
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return main.receiveEvent(type, receiver)
	}
	
	///
	
	private class Wave<E : Any, K : Any>(
		type: KClass<E>,
		private val keyer: (E) -> K,
		private val assistant: TemporalAssistant,
		channelLifetime: Long,
		channelLifetimeUnit: TimeUnit
	) : EventRadio.Wave<E, K>, Storage<K, EventChannel<E>> {
		private val emerger = WaveEmerger(type)
		
		private val channels = StorageKeeper(assistant, channelLifetime, channelLifetimeUnit, this)
		
		override fun forKey(key: K): EventDealer<E> {
			return channels.get(key)
		}
		
		override fun <T : E> receiveEvent(key: K, type: KClass<T>, receiver: (T) -> Unit): Cancelable {
			return channels.get(key).receiveEvent(type, receiver)
		}
		
		fun match(type: KClass<*>): Boolean {
			return emerger.match(type)
		}
		
		fun dispatchEvent(event: Any) {
			@Suppress("UNCHECKED_CAST")
			event as E
			val key = keyer(event)
			channels.touch(key)?.dispatchEvent(event)
		}
		
		override fun loadEntity(id: K): EventChannel<E> {
			return DefaultEventChannel(assistant, emerger, WaveChannelObserver(channels, id))
		}
		
		override fun flushEntity(id: K, entity: EventChannel<E>) {}
		
	}
	
	private class WaveChannelObserver<K : Any>(private val keeper: Keeper<K, *>, private val id: K) : EventChannelObserver {
		override fun observerReceiverAdded() {
			keeper.hold(id)
		}
		
		override fun observerReceiverRemoved() {
			keeper.release(id)
		}
	}
	
	private class WaveEmerger<E : Any>(type: KClass<E>) : AutoEventTypeEmerger<E>(type) {
		override fun convertTypes(list: MutableList<KClass<out E>>): Array<KClass<out E>> {
			@Suppress("UNCHECKED_CAST")
			return if (list.isEmpty()) (empty as Array<KClass<out E>>) else super.convertTypes(list)
		}
		
		private companion object {
			@JvmStatic
			private val empty = emptyArray<KClass<*>>()
		}
	}
}