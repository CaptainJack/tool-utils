package ru.capjack.tool.utils.events

import kotlin.reflect.KClass

interface EventTypeEmerger<E : Any> {
	fun emerge(type: KClass<*>): Array<KClass<out E>>
	
	fun match(type: KClass<*>): Boolean
}

