package ru.capjack.tool.utils

import ru.capjack.tool.lang.alsoTrue
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class Sluice(opened: Boolean = true) {
	private val lock = ReentrantReadWriteLock()
	
	@Volatile
	var opened = opened
		private set
	
	val passage: Lock
		get() = lock.readLock()
	
	fun open(): Boolean {
		return lock.writeLock().withLock {
			alsoTrue(!opened) { opened = true }
		}
	}
	
	fun close(): Boolean {
		return lock.writeLock().withLock {
			alsoTrue(opened) { opened = false }
		}
	}
	
	inline fun pass(code: () -> Unit): Boolean {
		if (opened) {
			passage.withLock {
				if (opened) {
					code()
					return true
				}
			}
		}
		return false
	}
}