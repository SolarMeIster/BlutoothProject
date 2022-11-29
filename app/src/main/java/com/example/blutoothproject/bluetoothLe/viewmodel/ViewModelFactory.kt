package com.example.blutoothproject.bluetoothLe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blutoothproject.App

class ViewModelFactory(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            ListBleDevicesViewModel::class.java -> ListBleDevicesViewModel(app.bleModel) as T
            else -> throw IllegalStateException("Error")
        }
    }
}