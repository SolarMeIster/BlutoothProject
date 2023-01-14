package com.example.blutoothproject

open class Observable {
    private val listeners = mutableSetOf<IObserver>()

    fun addListener(listener: IObserver) {
        listeners.add(listener)
    }

    fun removeListener(listener: IObserver) {
        listeners.remove(listener)
    }

    fun notifyChanged() {
        listeners.forEach {
            it.update()
        }
    }

}