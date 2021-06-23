package ru.capjack.tool.utils

expect fun interface Supplier<T> {
	fun get(): T
}