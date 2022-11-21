package com.example.blutoothproject.delete

import com.example.blutoothproject.ATMOSPHERE_PRESSURE
import kotlin.random.Random

class RandDataSocket : DataSocket {

    override fun getPressure(): MutableList<Int> {
        var i = 0
        val list = listOf(0, 0, 0, 0) as MutableList
        while (i < list.size) {
            val randomPressure = Random.nextInt(105, 150)
            list[i] = randomPressure - ATMOSPHERE_PRESSURE
            i++
        }
        return list
    }
}