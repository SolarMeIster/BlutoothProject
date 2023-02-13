package com.example.blutoothproject.bluetoothLe.bledata.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.IObserver
import com.example.blutoothproject.bluetoothLe.model.BleModel

class BleDataViewModel(private val bleModel: BleModel) : ViewModel(), IObserver {

    private val _characteristicValue = MutableLiveData<Map<String, Int>>()
    val characteristicValue: LiveData<Map<String, Int>> = _characteristicValue

    init {
        bleModel.addListener(this)
    }

    override fun update() {
        _characteristicValue.postValue(bleModel.characteristicValues)
    }

    fun disconnect(device: BluetoothDevice) {
        bleModel.disconnectFromDevice(device)
    }

    /*fun exceedPressure(): Boolean {
        return (allValueOfSettings[SettingsFragment.MAX] as Int) < _characteristicValue.value!!
    }*/

    override fun onCleared() {
        bleModel.removeListener(this)
    }
}