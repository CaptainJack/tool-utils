package ru.capjack.tool.utils.concurrency

expect class ArrayObjectPool<T : Any>(allocator: ObjectAllocator<T>, capacity: Int) : ObjectPool<T>