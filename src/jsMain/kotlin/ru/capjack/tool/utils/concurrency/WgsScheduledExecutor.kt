package ru.capjack.tool.utils.concurrency

import org.w3c.dom.WindowOrWorkerGlobalScope
import ru.capjack.tool.utils.Callable
import ru.capjack.tool.utils.CallableFunction0
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.CancelableCallable
import ru.capjack.tool.utils.ErrorCatcher
import ru.capjack.tool.utils.InstantTime

class WgsScheduledExecutor(
	errorCatcher: ErrorCatcher,
	instantTime: InstantTime,
	wgs: WindowOrWorkerGlobalScope
) : WgsExecutor(errorCatcher, instantTime, wgs), ScheduledExecutor {
	
	override fun schedule(delayMillis: Int, fn: () -> Unit): Cancelable {
		return schedule(delayMillis, CallableFunction0(fn))
	}
	
	override fun repeat(delayMillis: Int, fn: () -> Unit): Cancelable {
		return repeat(delayMillis, CallableFunction0(fn))
	}
	
	override fun schedule(delayMillis: Int, task: Callable<Unit>): Cancelable {
		return ScheduledTask(task, delayMillis, false)
	}
	
	override fun repeat(delayMillis: Int, task: Callable<Unit>): Cancelable {
		return ScheduledTask(task, delayMillis, true)
	}
	
	private inner class ScheduledTask(
		task: Callable<Unit>,
		delayMillis: Int,
		private val repeatable: Boolean
	) : CancelableCallable<Unit>(task) {
		
		private val id =
			if (repeatable) wgs.setInterval(::call, delayMillis)
			else wgs.setTimeout(::call, delayMillis)
		
		override fun cancel() {
			if (active) {
				super.cancel()
				if (repeatable)
					wgs.clearInterval(id)
				else
					wgs.clearTimeout(id)
			}
		}
		
		override fun call() {
			try {
				super.call()
			}
			catch (e: dynamic) {
				cancel()
				catchError(e)
			}
		}
	}
}