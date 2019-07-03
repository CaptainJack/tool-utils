package ru.capjack.tool.utils

inline fun wait(maxTimeoutMillis: Long, checkTimeoutMillis: Long = 100, check: () -> Boolean): Boolean {
	var b = check()
	if (b) {
		val time = System.currentTimeMillis()
		do {
			Thread.sleep(checkTimeoutMillis)
			b = check()
		}
		while (b && System.currentTimeMillis() - time < maxTimeoutMillis)
	}
	return b
}

inline fun wait(maxTimeoutSeconds: Int, checkTimeoutMillis: Long = 100, check: () -> Boolean): Boolean {
	return wait(maxTimeoutSeconds * 1000L, checkTimeoutMillis, check)
}