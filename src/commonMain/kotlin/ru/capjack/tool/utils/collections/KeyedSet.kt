package ru.capjack.tool.utils.collections

interface KeyedSet<in K : Any, out E : Any> : Set<E> {
	fun get(key: K): E?
	
	fun containsKey(key: K): Boolean
}

interface MutableKeyedSet<in K : Any, E : Any> : MutableCollection<E>, KeyedSet<K, E>


@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, E : Any> keyedSet(noinline keyer: (E) -> K): KeyedSet<K, E> = HashMapMutableKeyedSet(keyer)

@Suppress("NOTHING_TO_INLINE")
inline fun <K : Any, E : Any> mutableKeyedSet(noinline keyer: (E) -> K): MutableKeyedSet<K, E> = HashMapMutableKeyedSet(keyer)


abstract class AbstractMutableKeyedSet<in K : Any, E : Any>(private val keyer: (E) -> K) : MutableKeyedSet<K, E> {
	override fun contains(element: E): Boolean {
		return containsKey(keyer(element))
	}
	
	override fun add(element: E): Boolean {
		return doAdd(keyer(element), element)
	}
	
	override fun remove(element: E): Boolean {
		return doRemove(keyer(element))
	}
	
	protected abstract fun doAdd(key: K, element: E): Boolean
	
	protected abstract fun doRemove(key: K): Boolean
}


open class MapMutableKeyedSet<in K : Any, E : Any>(keyer: (E) -> K, private val map: MutableMap<K, E>) : AbstractMutableKeyedSet<K, E>(keyer) {
	override val size: Int
		get() = map.size
	
	override fun containsKey(key: K): Boolean {
		return map.containsKey(key)
	}
	
	override fun containsAll(elements: Collection<E>): Boolean {
		return map.values.containsAll(elements)
	}
	
	override fun isEmpty(): Boolean {
		return map.isEmpty()
	}
	
	override fun addAll(elements: Collection<E>): Boolean {
		return elements.fold(false) { m, e -> add(e) || m }
	}
	
	override fun clear() {
		map.clear()
	}
	
	override fun iterator(): MutableIterator<E> {
		return map.values.iterator()
	}
	
	override fun removeAll(elements: Collection<E>): Boolean {
		return elements.fold(false) { m, e -> remove(e) || m }
	}
	
	override fun retainAll(elements: Collection<E>): Boolean {
		var m = false
		val iterator = iterator()
		while (iterator.hasNext()) {
			val e = iterator.next()
			if (!elements.contains(e)) {
				iterator.remove()
				m = true
			}
		}
		return m
	}
	
	override fun get(key: K): E? {
		return map.remove(key)
	}
	
	override fun doAdd(key: K, element: E): Boolean {
		val prev = map.put(key, element)
		return prev == null || prev != element
	}
	
	override fun doRemove(key: K): Boolean {
		val prev = map.remove(key)
		return prev != null
	}
}

open class HashMapMutableKeyedSet<in K : Any, E : Any>(keyer: (E) -> K) : MapMutableKeyedSet<K, E>(keyer, hashMapOf())
