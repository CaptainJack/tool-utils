package ru.capjack.tool.utils.events

import ru.capjack.tool.utils.Cancelable
import kotlin.reflect.KClass

interface EventObservable<E : Any> {
	fun <T : E> observeEvent(type: KClass<T>, observer: (T) -> Unit): Cancelable
}

inline fun <reified T : Any> EventObservable<in T>.observeEvent(noinline observer: (T) -> Unit): Cancelable {
	return observeEvent(T::class, observer)
}