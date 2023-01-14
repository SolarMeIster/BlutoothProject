package com.example.blutoothproject.bluetoothLe.viewmodel

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.IObserver
import com.example.blutoothproject.bluetoothLe.model.BleModel

class ListBleDevicesViewModel(private val bleModel: BleModel) : ViewModel(), IObserver {

    private val _listDevice = MutableLiveData<List<ScanResult>>()
    val listDevice: LiveData<List<ScanResult>> = _listDevice

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

    override fun onCleared() {
        super.onCleared()
        bleModel.removeListener(this)
    }

    override fun update() {
        _listDevice.value = bleModel.scanResults
    }
}