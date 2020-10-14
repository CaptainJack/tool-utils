package ru.capjack.tool.utils.events

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

open class AutoEventTypeEmerger<E : Any>(private val type: KClass<E>) : EventTypeEmerger<E> {
	private val map = ConcurrentHashMap<KClass<*>, Array<KClass<out E>>>()
	
	private fun collectSuperClasses(type: KClass<*>, target: MutableList<KClass<out E>>) {
		if (type.isSubclassOf(this.type)) {
			@Suppress("UNCHECKED_CAST")
			target.add(type as KClass<out E>)
			type.superclasses.forEach { collectSuperClasses(it, target) }
		}
	}
	
	override fun emerge(type: KClass<*>): Array<KClass<out E>> {
		return map.getOrPut(type) {
			val list = mutableListOf<KClass<out E>>()
			collectSuperClasses(type, list)
			convertTypes(list)
		}
	}
	
	override fun match(type: KClass<*>): Boolean {
		return emerge(type).isNotEmpty()
	}
	
	protected open fun convertTypes(list: MutableList<KClass<out E>>): Array<KClass<out E>> {
		return list.toTypedArray()
	}
}