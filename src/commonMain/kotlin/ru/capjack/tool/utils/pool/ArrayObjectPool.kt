package ru.capjack.tool.utils.pool

expect class ArrayObjectPool<T : Any>(capacity: Int, allocator: ObjectAllocator<T>) : ObjectPool<T>