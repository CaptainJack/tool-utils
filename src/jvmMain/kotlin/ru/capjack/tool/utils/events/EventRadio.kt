package ru.capjack.tool.utils.events

import ru.capjack.tool.utils.Cancelable
import kotlin.reflect.KClass

interface EventRadio<E : Any> : EventChannel<E> {
	fun <T : E, K : Any> registerWave(type: KClass<T>, keyer: (T) -> K): Wave<T, K>
	
	interface Wave<E : Any, K : Any> {
		fun <T : E> receiveEvent(key: K, type: KClass<T>, receiver: (T) -> Unit): Cancelable
		
		fun forKey(key: K): EventDealer<E>
	}
}

inline fun <E : Any, reified T : E, K : Any> EventRadio<E>.registerWave(noinline keyer: (T) -> K): EventRadio.Wave<T, K> {
	return registerWave(T::class, keyer)
}

inline fun <reified T : Any, K : Any> EventRadio.Wave<in T, K>.receiveEvent(key: K, noinline receiver: (T) -> Unit): Cancelable {
	return receiveEvent(key, T::class, receiver)
}

