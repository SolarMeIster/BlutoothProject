package com.example.blutoothproject

import android.app.Application
import android.content.Context
import com.example.blutoothproject.bluetoothLe.model.BleModel
import com.example.blutoothproject.settings.model.IOSettings

class App : Application() {

    val bleModel = BleModel()
    val config = IOSettings()

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