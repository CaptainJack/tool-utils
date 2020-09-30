package ru.capjack.tool.utils.assistant

internal open class AssistantTask(private val code: () -> Unit) {
	open fun invoke() {
		code()
	}
}

