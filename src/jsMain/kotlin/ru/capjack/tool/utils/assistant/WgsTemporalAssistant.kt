package ru.capjack.tool.utils.assistant

import org.w3c.dom.WindowOrWorkerGlobalScope
import ru.capjack.tool.utils.Cancelable
import ru.capjack.tool.utils.ErrorCatcher
import ru.capjack.tool.utils.InstantTime

class WgsTemporalAssistant(
	errorCatcher: ErrorCatcher,
	instantTime: InstantTime,
	wgs: WindowOrWorkerGlobalScope
) : WgsAssistant(errorCatcher, instantTime, wgs), TemporalAssistant {
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		return ScheduledTask(code, delayMillis, false)
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		return ScheduledTask(code, delayMillis, true)
	}
	
	private inner class ScheduledTask(
		code: () -> Unit,
		delayMillis: Int,
		private val repeatable: Boolean
	) : CancelableAssistantTask(code) {
		
		private val id =
			if (repeatable) wgs.setInterval(::invoke, delayMillis) else wgs.setTimeout(::invoke, delayMillis)
		
		override fun cancel() {
			if (!canceled) {
				super.cancel()
				if (repeatable) wgs.clearInterval(id) else wgs.clearTimeout(id)
			}
		}
		
		override fun invoke() {
			try {
				super.invoke()
			}
			catch (e: dynamic) {
				cancel()
				catchError(e)
			}
		}
	}
}