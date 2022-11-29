package com.example.blutoothproject.bluetoothLe.viewmodel

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.bluetoothLe.model.BleModel
import com.example.blutoothproject.bluetoothLe.model.ScanResultsListener

class ListBleDevicesViewModel(private val bleModel: BleModel) : ViewModel() {

    private val _listDevice = MutableLiveData<List<ScanResult>>()

    val listDevice: LiveData<List<ScanResult>> = _listDevice

    private val listener: ScanResultsListener = {
        _listDevice.value = it
    }

    init {
        bleModel.addListener(listener)
    }

    fun checkBluetooth() {
        //bleModel.check()
    }

    fun connect(scanResultDevice: BluetoothDevice) {
        bleModel.connectToDevice(scanResultDevice)
    }

    fun search() {
        bleModel.searchDevices()
    }

    override fun onCleared() {
        super.onCleared()
        bleModel.removeListener(listener)
    }


}