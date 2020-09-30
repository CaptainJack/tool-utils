package ru.capjack.tool.utils.keeper

abstract class AbstractEntity : InternalEntity {
	final override val lock = InternalEntityLock()
}