package com.example.blutoothproject

import android.app.Application
import android.content.Context
import com.example.blutoothproject.bluetoothLe.model.BleModel

class App : Application() {

    val bleModel = BleModel()

    init{
        instance = this
    }
    companion object {
        private lateinit var instance: App
        fun getInstance(): Context {
            return instance.applicationContext
        }
    }
}