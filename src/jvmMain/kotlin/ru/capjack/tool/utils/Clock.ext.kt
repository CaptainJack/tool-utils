package ru.capjack.tool.utils

import java.time.Clock
import java.time.LocalDateTime

fun Clock.nowLocalDateTime(shiftSeconds: Int = 0): LocalDateTime {
	val now = this.instant()
	val offset = this.zone.rules.getOffset(now)
	return LocalDateTime.ofEpochSecond(now.epochSecond + shiftSeconds, now.nano, offset)
}

fun Clock.nowLocalDateTimeWithoutMillis(shiftSeconds: Int = 0): LocalDateTime {
	val now = this.instant()
	val offset = this.zone.rules.getOffset(now)
	return LocalDateTime.ofEpochSecond(now.epochSecond + shiftSeconds, 0, offset)
}