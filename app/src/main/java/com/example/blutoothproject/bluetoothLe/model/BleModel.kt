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
    var characteristicValue = 0

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager =
            App.getInstance().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val bleFilter = ScanFilter.Builder()
        .build()

    private val scanResultCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
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
            val deviceAddress = gatt?.device?.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    gatt?.discoverServices()
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.i("GattCallback", "Disconnect device: $deviceAddress")
                    gatt?.close()
                }
            } else {
                Log.i("GattCallback", "Error $status encountered for device: $deviceAddress")
                gatt?.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            with(gatt) {
                if (services.isEmpty()) {
                    Log.i(
                        "BluetoothServices",
                        "This device ${device.name} don't have services"
                    )
                    return
                }
                services.forEach { service ->
                    val characteristics = service.characteristics.joinToString(
                        separator = "\n--",
                        prefix = "--"
                    ) { it.uuid.toString() }
                    Log.i(
                        "BluetoothService",
                        "\nService: ${service.uuid}\nCharacteristics:\n$characteristics"
                    )
                }

            }

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            val characteristicByteArray = value.copyOf()
            characteristicValue = characteristicByteArray[1].toInt()
            Log.i(
                "onCharacteristicChanged",
                "Device ${gatt.device.name}, Characteristic: ${characteristic.uuid}, Value: ${characteristicByteArray[1]}"
            )
        }
    }

    // старт сканирования устройств
    fun startSearchDevices() {
        bleScanner.startScan(listOf<ScanFilter>(bleFilter), scanSetting, scanResultCallback)
    }

    // стоп сканирования устройств
    fun stopSearchDevices() {
        bleScanner.stopScan(scanResultCallback)
    }

    // проверка на включенный bluetooth
    fun check(): Boolean = bluetoothAdapter.isEnabled

    // подключение к устройству
    fun connectToDevice(scanResultDevice: BluetoothDevice) {
        scanResultDevice.connectGatt(App.getInstance(), false, gattCallback)
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