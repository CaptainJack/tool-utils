package ru.capjack.tool.utils.concurrency

import org.w3c.dom.WindowOrWorkerGlobalScope
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.ErrorCatcher
import ru.capjack.tool.utils.InstantTime
import ru.capjack.tool.utils.collections.ArrayQueue

open class WgsAssistant(
	private val errorCatcher: ErrorCatcher,
	private val instantTime: InstantTime,
	protected val wgs: WindowOrWorkerGlobalScope
) : Assistant {
	
	private val tasks = ArrayQueue<AssistantTask>()
	private val runFn = ::run
	private var runId: Int = 0
	private var idle = true
	
	override fun execute(code: () -> Unit) {
		queue(AssistantTask(code))
	}
	
	override fun charge(code: () -> Unit): Cancelable {
		return CancelableAssistantTask(code).also { queue(it) }
	}
	
	private fun queue(task: AssistantTask) {
		tasks.add(task)
		activate()
	}
	
	private fun activate() {
		if (idle) {
			idle = false
			runId = wgs.setInterval(runFn)
		}
	}
	
	private fun deactivate() {
		if (!idle) {
			wgs.clearInterval(runId)
			runId = 0
			idle = true
		}
	}
	
	private fun run() {
		val startTime = instantTime.now()
		var i = 0
		val l = tasks.size
		
		do {
			try {
				tasks.poll()!!.invoke()
			}
			catch (e: dynamic) {
				catchError(e)
			}
			++i
		}
		while (i < l && instantTime.now() - startTime >= 20.0)
		
		if (tasks.isEmpty()) {
			deactivate()
		}
	}
	
	protected fun catchError(e: dynamic) {
		errorCatcher.catchError(e)
	}
}
