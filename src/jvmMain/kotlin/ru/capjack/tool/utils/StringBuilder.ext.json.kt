package ru.capjack.tool.utils

fun StringBuilder.appendJsonValue(v: Any?): StringBuilder {
	return when (v) {
		is Number       -> appendJsonValue(v.toString())
		is String       -> appendJsonValue(v)
		is Iterable<*>  -> appendJsonValue(v)
		is Array<*>     -> appendJsonValue(v)
		is Map<*, *>    -> appendJsonValue(v)
		is Boolean      -> append(v)
		is IntArray     -> appendJsonValue(v)
		is LongArray    -> appendJsonValue(v)
		is DoubleArray  -> appendJsonValue(v)
		is BooleanArray -> appendJsonValue(v)
		is Enum<*>      -> appendJsonValue(v.name)
		else            -> append(v.toString())
	}
}

fun StringBuilder.appendJsonValue(v: String): StringBuilder {
	return append('"').appendJsonEscapedString(v).append('"')
}

fun StringBuilder.appendJsonEscapedString(v: String): StringBuilder {
	for (c in v) {
		when (c) {
			'"'      -> append("\\\"")
			'\\'     -> append("\\\\")
			'\n'     -> append("\\n")
			'\r'     -> append("\\r")
			'\t'     -> append("\\t")
			'\b'     -> append("\\b")
			'\u000c' -> append("\\f")
			else     ->
				if (c in '\u0000'..'\u001F' || c in '\u007F'..'\u009F' || c in '\u2000'..'\u20FF')
					append("\\u").append(Integer.toHexString(c.toInt()).padStart(4, '0'))
				else
					append(c)
		}
	}
	return this
}

fun StringBuilder.appendJsonValue(v: Iterable<*>): StringBuilder {
	return appendJsonArray {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			appendJsonValue(e)
		}
	}
}

fun StringBuilder.appendJsonValue(v: Array<*>): StringBuilder {
	return appendJsonArray {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			appendJsonValue(e)
		}
	}
}

fun StringBuilder.appendJsonValue(v: IntArray): StringBuilder {
	return appendJsonArray {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			append(e)
		}
	}
}

fun StringBuilder.appendJsonValue(v: LongArray): StringBuilder {
	return appendJsonArray {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			append(e)
		}
	}
}


fun StringBuilder.appendJsonValue(v: DoubleArray): StringBuilder {
	return appendJsonArray {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			append(e)
		}
	}
}


fun StringBuilder.appendJsonValue(v: BooleanArray): StringBuilder {
	return appendJsonArray {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			append(e)
		}
	}
}

fun StringBuilder.appendJsonValue(v: Map<*, *>): StringBuilder {
	return appendJsonObject {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			appendJsonObjectEntry(e.key.toString(), e.value)
		}
	}
}

inline fun <T> StringBuilder.appendJsonValue(v: Iterable<T>, key: (T) -> String, value: (T) -> Any?): StringBuilder {
	return appendJsonObject {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			appendJsonObjectEntry(key(e), value(e))
		}
	}
}

inline fun <T> StringBuilder.appendJsonValue(v: Array<T>, key: (T) -> String, value: (T) -> Any?): StringBuilder {
	return appendJsonObject {
		var n = false
		for (e in v) {
			if (n) append(',') else n = true
			appendJsonObjectEntry(key(e), value(e))
		}
	}
}

inline fun StringBuilder.appendJsonArray(block: StringBuilder.() -> Unit): StringBuilder {
	append('[')
	block()
	append(']')
	return this
}

inline fun StringBuilder.appendJsonObject(block: StringBuilder.() -> Unit): StringBuilder {
	append('{')
	block()
	append('}')
	return this
}

fun StringBuilder.appendJsonObjectEntry(key: String, value: Any?): StringBuilder {
	return appendJsonValue(key).append(":").appendJsonValue(value)
}

fun StringBuilder.appendJsonObjectEntry(key: String, value: String): StringBuilder {
	return appendJsonValue(key).append(":").appendJsonValue(value)
}

fun StringBuilder.appendJsonObjectEntry(key: String, value: Int): StringBuilder {
	return appendJsonValue(key).append(":").append(value)
}

fun StringBuilder.appendJsonObjectEntry(key: String, value: Long): StringBuilder {
	return appendJsonValue(key).append(":").append(value)
}

inline fun StringBuilder.appendJsonObjectEntry(key: String, value: StringBuilder.() -> Unit): StringBuilder {
	appendJsonValue(key).append(":")
	value()
	return this
}


fun StringBuilder.appendJsonObjectEntryNext(key: String, value: Any?): StringBuilder {
	return append(',').appendJsonObjectEntry(key, value)
}

fun StringBuilder.appendJsonObjectEntryNext(key: String, value: String): StringBuilder {
	return append(',').appendJsonObjectEntry(key, value)
}

fun StringBuilder.appendJsonObjectEntryNext(key: String, value: Int): StringBuilder {
	return append(',').appendJsonObjectEntry(key, value)
}

fun StringBuilder.appendJsonObjectEntryNext(key: String, value: Long): StringBuilder {
	return append(',').appendJsonObjectEntry(key, value)
}

inline fun StringBuilder.appendJsonObjectEntryNext(key: String, value: StringBuilder.() -> Unit): StringBuilder {
	return append(',').appendJsonObjectEntry(key, value)
}