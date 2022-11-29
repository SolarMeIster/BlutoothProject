package com.example.blutoothproject.bluetoothLe.view

import android.app.Service
import android.content.Intent
import android.os.IBinder

class BleService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}