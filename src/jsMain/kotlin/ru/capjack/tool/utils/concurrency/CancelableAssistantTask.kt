package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable

internal open class CancelableAssistantTask(code: () -> Unit) : AssistantTask(code),
	Cancelable {
	var canceled = false
		private set
	
	override fun cancel() {
		canceled = true
	}
	
	override fun invoke() {
		if (!canceled) {
			super.invoke()
		}
	}
}