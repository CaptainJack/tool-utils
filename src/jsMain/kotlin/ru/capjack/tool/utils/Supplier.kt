package ru.capjack.tool.utils

actual fun interface Supplier<T> {
	actual fun get(): T
}