package com.example.blutoothproject.bluetoothLe.viewmodel

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BleService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}