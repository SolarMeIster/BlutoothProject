package com.example.blutoothproject.bluetoothLe.model

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.blutoothproject.App

typealias ScanResultsListener = (result: List<ScanResult>) -> Unit

class BleModel {

    private val listeners = mutableSetOf<ScanResultsListener>()

    private val scanSetting = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    val scanResults: MutableList<ScanResult> = mutableListOf()

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = App.getInstance().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val bleFilter = ScanFilter.Builder()
        .build()

    private val scanResultCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) {
                scanResults[indexQuery] = result
            } else {
                Log.i(
                    "ScanCallback",
                    "Found BLE device! Name: ${result.device.name ?: "Unnamed"}, address: ${result.device.address}"
                )
                scanResults.add(result)
            }
            notifyChanged()
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(SCAN_ERROR, "Ошибка сканирования")
        }

    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            super.onCharacteristicChanged(gatt, characteristic, value)
        }
    }

    fun searchDevices() {
        bleScanner.startScan(listOf<ScanFilter>(bleFilter), scanSetting, scanResultCallback)
    }



    fun connectToDevice(scanResultDevice: BluetoothDevice) {
        //scanResultDevice.connectGatt()
    }

    fun addListener(listener: ScanResultsListener) {
        listeners.add(listener)
        listener.invoke(scanResults)
    }

    fun removeListener(listener: ScanResultsListener) {
        listeners.remove(listener)
    }

    private fun notifyChanged() {
        listeners.forEach { it.invoke(scanResults) }
    }

    companion object {
        const val SCAN_ERROR = "SCAN ERROR"
    }
}