package ru.capjack.tool.utils.concurrency

import java.util.concurrent.ExecutorService

class ExecutorAssistant(executor: ExecutorService) : AbstractExecutorAssistant<ExecutorService>(executor), Assistant

