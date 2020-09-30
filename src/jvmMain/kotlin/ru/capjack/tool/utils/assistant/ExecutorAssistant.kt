package ru.capjack.tool.utils.assistant

import java.util.concurrent.ExecutorService

class ExecutorAssistant(executor: ExecutorService) : AbstractExecutorAssistant<ExecutorService>(executor), Assistant

