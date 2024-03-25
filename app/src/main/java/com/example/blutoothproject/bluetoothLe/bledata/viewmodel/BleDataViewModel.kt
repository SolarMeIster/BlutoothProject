package com.example.blutoothproject.bluetoothLe.bledata.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.BleStruct
import com.example.blutoothproject.IObserver
import com.example.blutoothproject.bluetoothLe.model.BleModel

@SuppressLint("MissingPermission")
class BleDataViewModel(private val bleModel: BleModel, private val savedStateHandle: SavedStateHandle) : ViewModel(), IObserver {

    private val _characteristicValue = MutableLiveData<Map<BluetoothGatt, BleStruct>>()
    val characteristicValue: LiveData<Map<BluetoothGatt, BleStruct>> = _characteristicValue

    init {
        bleModel.addListener(this)
    }

    override fun update() {
        _characteristicValue.postValue(bleModel.characteristicValues)
    }

    fun notifyChanged() {
        bleModel.notifyChanged()
    }

    fun writeChar() {
        bleModel.writeCharacteristic()
    }

    fun disableNotify(gatt: BluetoothGatt) {
        Log.i("DisableNotification", "disable notify with Device: ${gatt.device.name ?: "Unnamed"}, Address: ${gatt.device.address}")
        bleModel.enableOrDisableNotification(gatt, bleModel::disableNotification)
    }

    fun enableNotify(gatt: BluetoothGatt) {
        Log.i("EnableNotification", "enable notify with Device: ${gatt.device.name ?: "Unnamed"}, Address: ${gatt.device.address}")
        bleModel.enableOrDisableNotification(gatt, bleModel::enableNotification)
    }

    fun disconnect(device: BluetoothDevice) {
        bleModel.disconnectFromDevice(device)
    }

    /*fun exceedPressure(): Boolean {
        return (allValueOfSettings[SettingsFragment.MAX] as Int) < _characteristicValue.value!!
    }*/

    override fun onCleared() {
        bleModel.removeListener(this)
        Log.i("Lifecycle", "onCleared")
    }
}