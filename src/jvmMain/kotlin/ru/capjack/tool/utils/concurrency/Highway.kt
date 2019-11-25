package ru.capjack.tool.utils.concurrency

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

class Highway(opened: Boolean) {
	private val lock = ReentrantReadWriteLock()
	
	@Volatile
	var opened = opened
		private set
	
	val passage: Lock
		get() = lock.readLock()
	
	fun open(): Boolean {
		lock.writeLock().withLock {
			if (!opened) {
				opened = true
				return true
			}
		}
		return false
	}
	
	fun close(): Boolean {
		lock.writeLock().withLock {
			if (opened) {
				opened = false
				return true
			}
		}
		return false
	}
	
	inline fun drive(block: () -> Unit): Boolean {
		if (opened) {
			passage.withLock {
				if (opened) {
					block()
					return true
				}
			}
		}
		return false
	}
}