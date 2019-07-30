package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.CallableFunction1
import ru.capjack.tool.utils.CallableFunction2
import ru.capjack.tool.utils.CallableFunction3
import ru.capjack.tool.utils.Cancelable

fun <A> Executor.execute(arg: A, fn: (A) -> Unit) {
	execute(CallableFunction1(fn, arg))
}

fun <A, B> Executor.execute(arg1: A, arg2: B, fn: (A, B) -> Unit) {
	execute(CallableFunction2(fn, arg1, arg2))
}

fun <A, B, C> Executor.execute(arg1: A, arg2: B, arg3: C, fn: (A, B, C) -> Unit) {
	execute(CallableFunction3(fn, arg1, arg2, arg3))
}

fun <A> Executor.submit(arg: A, fn: (A) -> Unit): Cancelable {
	return submit(CallableFunction1(fn, arg))
}

fun <A, B> Executor.submit(arg1: A, arg2: B, fn: (A, B) -> Unit): Cancelable {
	return submit(CallableFunction2(fn, arg1, arg2))
}

fun <A, B, C> Executor.submit(arg1: A, arg2: B, arg3: C, fn: (A, B, C) -> Unit): Cancelable {
	return submit(CallableFunction3(fn, arg1, arg2, arg3))
}
