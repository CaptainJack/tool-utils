package ru.capjack.tool.utils.events

interface EventChannel<E : Any> : EventDispatcher<E>, EventObservable<E>