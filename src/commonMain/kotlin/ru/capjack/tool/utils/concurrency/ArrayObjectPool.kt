package ru.capjack.tool.utils.concurrency

expect class ArrayObjectPool<T : Any>(capacity: Int, allocator: ObjectAllocator<T>) : ObjectPool<T>