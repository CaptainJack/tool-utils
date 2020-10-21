package ru.capjack.tool.utils.assistant

import ru.capjack.tool.utils.Cancelable
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ExecutorClockAssistant(executor: ScheduledExecutorService, private val clock: Clock) : ExecutorTemporalAssistant(executor), ClockAssistant {
	override fun schedule(point: LocalDateTime, code: () -> Unit): Cancelable {
		val delay = LocalDateTime.now(clock).until(point, ChronoUnit.NANOS).coerceAtLeast(0)
		return schedule(delay, TimeUnit.NANOSECONDS, code)
	}
}