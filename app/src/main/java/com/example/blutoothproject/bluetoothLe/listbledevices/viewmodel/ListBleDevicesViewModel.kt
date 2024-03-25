package com.example.blutoothproject.bluetoothLe.listbledevices.viewmodel

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.IObserver
import com.example.blutoothproject.bluetoothLe.model.BleModel
import java.util.concurrent.ConcurrentHashMap

class ListBleDevicesViewModel(private val bleModel: BleModel) : ViewModel(), IObserver {

    private val _listDevice = MutableLiveData<List<ScanResult>>()
    val listDevice: LiveData<List<ScanResult>> = _listDevice

    private val _listConnectedDevices = MutableLiveData<ConcurrentHashMap<BluetoothDevice, BluetoothGatt>>()
    val listConnectedDevices: LiveData<ConcurrentHashMap<BluetoothDevice, BluetoothGatt>> = _listConnectedDevices

    init {
        bleModel.addListener(this)
    }

    fun checkBluetooth() = bleModel.check()

    fun connect(scanResultDevice: BluetoothDevice) {
        bleModel.connectToDevice(scanResultDevice)
    }

    fun startSearch() {
        bleModel.startSearchDevices()
    }

    fun stopSearch() {
        bleModel.stopSearchDevices()
    }

    fun disableNotify(gatt: BluetoothGatt) {
        //Log.i("DisableNotification", "disable notify with Device: ${gatt.device.name ?: "Unnamed"}, Address: ${gatt.device.address}")
        bleModel.enableOrDisableNotification(gatt, bleModel::disableNotification)
    }

    fun enableNotify(gatt: BluetoothGatt) {
        //Log.i("EnableNotification", "enable notify with Device: ${gatt.device.name ?: "Unnamed"}, Address: ${gatt.device.address}")
        bleModel.enableOrDisableNotification(gatt, bleModel::enableNotification)
    }

    override fun onCleared() {
        super.onCleared()
        bleModel.removeListener(this)
    }

    override fun update() {
        _listDevice.postValue(bleModel.scanResults)
        _listConnectedDevices.postValue(bleModel.gattDevices)
    }
}