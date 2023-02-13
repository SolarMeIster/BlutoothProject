package com.example.blutoothproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.blutoothproject.bluetoothLe.bledata.viewmodel.BleDataViewModel
import com.example.blutoothproject.bluetoothLe.listbledevices.viewmodel.ListBleDevicesViewModel
import com.example.blutoothproject.settings.viewmodel.SettingViewModel

class ViewModelFactory(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when(modelClass) {
            ListBleDevicesViewModel::class.java -> ListBleDevicesViewModel(app.bleModel) as T
            BleDataViewModel::class.java -> BleDataViewModel(app.bleModel) as T
            SettingViewModel::class.java -> SettingViewModel(app.config) as T
            else -> throw IllegalStateException("Error")
        }
    }
}