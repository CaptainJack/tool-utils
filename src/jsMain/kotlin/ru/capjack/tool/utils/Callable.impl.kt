package ru.capjack.tool.utils

class CallableFunction0<R>(private val fn: () -> R) : Callable<R> {
	override fun call() = fn()
}

class CallableFunction1<A, R>(private val fn: (A) -> R, private val arg: A) : Callable<R> {
	override fun call() = fn(arg)
}

class CallableFunction2<A, B, R>(private val fn: (A, B) -> R, private val arg1: A, private val arg2: B) : Callable<R> {
	override fun call() = fn(arg1, arg2)
}

class CallableFunction3<A, B, C, R>(private val fn: (A, B, C) -> R, private val arg1: A, private val arg2: B, private val arg3: C) : Callable<R> {
	override fun call() = fn(arg1, arg2, arg3)
}

open class CancelableCallable<R>(private val task: Callable<R>) : Callable<R?>, Cancelable {
	protected var active = true
		private set
	
	override fun cancel() {
		active = false
	}
	
	override fun call(): R? {
		return if (active) task.call() else null
	}
}