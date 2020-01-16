package ru.capjack.tool.utils.concurrency

internal open class AssistantTask(private val code: () -> Unit) {
	open fun invoke() {
		code()
	}
}

