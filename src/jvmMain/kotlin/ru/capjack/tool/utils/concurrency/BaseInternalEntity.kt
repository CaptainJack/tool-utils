package ru.capjack.tool.utils.concurrency

open class BaseInternalEntity : InternalEntity {
	final override val lock = InternalEntityLock()
}