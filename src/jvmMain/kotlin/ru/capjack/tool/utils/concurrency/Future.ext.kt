package ru.capjack.tool.utils.concurrency

import ru.capjack.tool.utils.Cancelable
import java.util.concurrent.Future

fun Future<*>.asCancelable() = Cancelable {
	cancel(false)
}