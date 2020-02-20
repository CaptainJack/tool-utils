package ru.capjack.tool.utils.concurrency

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestArrayObjectPool {
	
	@Test
	fun concurrent() {
		
		class Stub {
			val using = AtomicBoolean(false)
		}
		
		val creates = AtomicInteger()
		val uses = AtomicInteger()
		val clears = AtomicInteger()
		val disposes = AtomicInteger()
		
		val allocator = object : ObjectAllocator<Stub> {
			override fun produceInstance(): Stub {
				creates.getAndIncrement()
				return Stub()
			}
			
			override fun clearInstance(instance: Stub) {
				clears.getAndIncrement()
			}
			
			override fun disposeInstance(instance: Stub) {
				disposes.getAndIncrement()
			}
		}
		
		val tasks = 100
		val times = 10
		val pool = ArrayObjectPool(allocator, 64)
		val executor = Executors.newFixedThreadPool(10)
		
		repeat(tasks) {
			executor.execute {
				repeat(times) {
					pool.use {
						val actual = it.using.getAndSet(true)
						assertFalse(actual)
						uses.getAndIncrement()
						Thread.sleep(Random.nextLong(1, 10))
						it.using.set(false)
					}
				}
			}
		}
		
		executor.shutdown()
		executor.awaitTermination(10, TimeUnit.SECONDS)
		pool.clear()
		
		/*
		println("creates $creates")
		println("disposes $disposes")
		println("uses $uses")
		println("clears $clears")
		*/
		
		val expectedUses = tasks * times
		val expectedClears = expectedUses - creates.get()
		
		assertEquals(creates.get(), disposes.get(), "Creates and disposes")
		assertEquals(expectedUses, uses.get(), "Uses")
		assertEquals(expectedClears, clears.get(), "Clears")
	}
}