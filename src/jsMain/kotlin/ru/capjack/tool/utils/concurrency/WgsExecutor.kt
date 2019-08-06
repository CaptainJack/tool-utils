package ru.capjack.tool.utils.concurrency

import org.w3c.dom.WindowOrWorkerGlobalScope
import ru.capjack.tool.utils.Callable
import ru.capjack.tool.utils.CallableFunction0
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.CancelableCallable
import ru.capjack.tool.utils.ErrorCatcher
import ru.capjack.tool.utils.InstantTime
import ru.capjack.tool.utils.collections.ArrayQueue

open class WgsExecutor(
	private val errorCatcher: ErrorCatcher,
	private val instantTime: InstantTime,
	protected val wgs: WindowOrWorkerGlobalScope
) : Executor {
	
	private val queue = ArrayQueue<Callable<*>>()
	private val runner = ::run
	private var runnerId: Int = 0
	private var idle = true
	
	override fun execute(fn: () -> Unit) {
		add(CallableFunction0(fn))
	}
	
	override fun execute(task: Callable<Unit>) {
		add(task)
	}
	
	override fun submit(fn: () -> Unit): Cancelable {
		return addCancelable(CallableFunction0(fn))
	}
	
	override fun submit(task: Callable<Unit>): Cancelable {
		return addCancelable(task)
	}
	
	private fun add(task: Callable<*>) {
		queue.add(task)
		activate()
	}
	
	private fun addCancelable(task: Callable<Unit>): Cancelable {
		return CancelableCallable(task).also(::add)
	}
	
	private fun activate() {
		if (idle) {
			idle = false
			runnerId = wgs.setInterval(runner)
		}
	}
	
	private fun deactivate() {
		if (!idle) {
			wgs.clearInterval(runnerId)
			runnerId = 0
			idle = true
		}
	}
	
	private fun run() {
		val startTime = instantTime.now()
		var i = 0
		val l = queue.size
		
		do {
			try {
				queue.poll()!!.call()
			}
			catch (e: dynamic) {
				catchError(e)
			}
			++i
		}
		while (i < l && instantTime.now() - startTime >= 20.0)
		
		if (queue.isEmpty()) {
			deactivate()
		}
	}
	
	protected fun catchError(e: dynamic) {
		errorCatcher.catch(e)
	}
}
