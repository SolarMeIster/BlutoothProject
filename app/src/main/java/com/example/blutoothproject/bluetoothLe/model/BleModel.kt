package com.example.blutoothproject.bluetoothLe.model

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import com.example.blutoothproject.App
import com.example.blutoothproject.Observable
import com.example.blutoothproject.bluetoothLe.*
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

@SuppressLint("MissingPermission")
class BleModel : Observable() {

    private val gattDevices = ConcurrentHashMap<BluetoothDevice, BluetoothGatt>()
    private var pendingOperation: BleOperationType? = null
    private val operationsQueue = ConcurrentLinkedQueue<BleOperationType>()
    private val scanSetting = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    val scanResults: MutableList<ScanResult> =
        mutableListOf() // данные по устройствам (идут в ListBleDevicesFragment)
    var characteristicValues =
        mutableMapOf<String, Int>() // данные по значениям давления подключенных устройств (идут в BleDataFragment)

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

        // колбэк, который возвращает найтденные устройства
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
            Log.e(SCAN_ERROR, "Error of scanning")
        }

    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device?.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    gattDevices[gatt.device] = gatt
                    gatt.discoverServices()
                } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    Log.i("GattCallback", "Disconnect device: $deviceAddress")
                    gatt.services.forEach { service ->
                        service.characteristics.forEach { characteristic ->
                            if (characteristic.uuid == UUID.fromString(USER_UUID_STM)) {
                                disableNotification(characteristic, gatt)
                            }
                        }
                    }
                    disconnectFromDevice(gatt.device)
                }
            } else {
                Log.i("GattCallback", "Error $status encountered for device: $deviceAddress")
                if (pendingOperation is Connect) {
                    endOfOperation()
                }
                gatt.services.forEach { service ->
                    service.characteristics.forEach { characteristic ->
                        if (characteristic.uuid == UUID.fromString(USER_UUID_STM)) {
                            disableNotification(characteristic, gatt)
                        }
                    }
                }
                disconnectFromDevice(gatt.device)
            }
        }

        // колбэк, который показывает какие сервисы, характеристики и т.д. находятся на BLE устройстве
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
                    if (pendingOperation is Connect) {
                        endOfOperation()
                    }
                    service.characteristics.forEach { characteristic ->
                        if (characteristic.uuid == UUID.fromString(USER_UUID_STM) || characteristic.uuid == UUID.fromString(
                                USER_UUID_TEXAS
                            )
                        ) {
                            enableNotification(characteristic, gatt)

                        }
                    }
                }
            }
        }

        // колбэк, котрый срабатывает после получения доступа из метода enableNotification
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (gatt != null) {
                when (gatt.device?.name) {
                    "HRSTM" -> {
                        changeVisualisationData(gatt, characteristic, 1)
                    }
                    "SimpleBLEPeripheral" -> {
                        changeVisualisationData(gatt, characteristic, 0)
                    }
                }
            }
        }
    }

    private fun changeVisualisationData(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        index: Int
    ) {
        val characteristicByteArray = characteristic.value.copyOf()
        characteristicValues[gatt.device.name] = characteristicByteArray[index].toInt()
        Log.i(
            "onCharacteristicChanged",
            "Device ${gatt.device.name}, Characteristic: ${characteristic.uuid}, Value: ${characteristicByteArray[index]}"
        )
        notifyChanged()
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

    // подключение телефона к BLE устройству
    fun connectToDevice(device: BluetoothDevice) {
        if (gattDevices.containsKey(device)) {
            Log.i("Connect", "Device: ${device.name} has already connected")
            return
        }
        addToQueue(Connect(device))
    }

    // отключение BLE устройства от телефона
    fun disconnectFromDevice(device: BluetoothDevice) {
        if (gattDevices.containsKey(device)) {
            addToQueue(Disconnect(device))
            return
        }
        Log.i("Disconnect", "This device is not connected")
    }

    // получение доступа к уведомлениям с BLE устройства
    fun enableNotification(characteristic: BluetoothGattCharacteristic, gatt: BluetoothGatt) {
        with(gatt) {
            if (gattDevices.containsKey(device) && (characteristic.isNotifiable() || characteristic.isIndictable())) {
                addToQueue(EnableNotification(device, characteristic))
            } else if (!gattDevices.containsKey(device)) {
                Log.e("EnableNotification", "Device is not connected")
            } else {
                Log.e(
                    "EnableNotification",
                    "${characteristic.uuid} is not supported notification/indication"
                )
            }
        }
    }

    // отключение доступа к уведомлениям с BLE устройства
    private fun disableNotification(
        characteristic: BluetoothGattCharacteristic,
        gatt: BluetoothGatt
    ) {
        with(gatt) {
            if (gattDevices.containsKey(device) && (characteristic.isNotifiable() || characteristic.isIndictable())) {
                addToQueue(DisableNotification(device, characteristic))
            } else if (!gattDevices.containsKey(device)) {
                Log.e(
                    "DisableNotification",
                    "Not connected to ${device.address}, cannot disable notifications"
                )
            } else {
                Log.e(
                    "DisableNotification",
                    "${characteristic.uuid} is not supported notification/indication"
                )
            }
        }
    }

    @Synchronized
    private fun addToQueue(operation: BleOperationType) {
        operationsQueue.add(operation)
        if (pendingOperation == null) {
            doWithOperation()
        }
    }

    @Synchronized
    private fun endOfOperation() {
        pendingOperation = null
        if (operationsQueue.isNotEmpty()) {
            doWithOperation()
        }
    }

    @Synchronized
    private fun doWithOperation() {
        if (pendingOperation != null) {
            Log.e("Operation", "doNextOperation() called when an operation is pending! Aborting.")
            return
        }
        val operation = operationsQueue.poll() ?: run {
            Log.e("Operation", "Error with operation queue")
            return
        }
        pendingOperation = operation
        if (operation is Connect) {
            operation.device.connectGatt(App.getInstance(), false, gattCallback)
            return
        }
        val gatt = gattDevices[operation.device] ?: run {
            Log.e("Operation", "Absence device in gattDevice")
            endOfOperation()
            return
        }
        when (operation) {
            is Disconnect -> {
                gattDevices.remove(operation.device)
                gatt.close()
                endOfOperation()
                Log.i("Operation", "Disconnect device: ${operation.device.name}")
            }
            is EnableNotification -> {
                with(operation) {
                    val cccDescriptorUUID = UUID.fromString(USER_DESCRIPTOR_UUID)
                    val payload = when {
                        characteristic.isIndictable() -> BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
                        characteristic.isNotifiable() -> BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        else -> {
                            Log.e("Notification", "Error")
                            return
                        }
                    }
                    characteristic.getDescriptor(cccDescriptorUUID).let { cccDescriptor ->
                        if (!gatt.setCharacteristicNotification(characteristic, true)) {
                            Log.e("Notification", "Error descriptor")
                            return
                        }
                        Log.i("EnableNotification", "Device: $device is enabled notification")
                        cccDescriptor.value = payload
                        gatt.writeDescriptor(cccDescriptor)
                        characteristicValues[gatt.device.name] = 0
                        endOfOperation()
                    }
                }
            }
            is DisableNotification -> {
                with(operation) {
                    val cccDescriptorUUID = UUID.fromString(USER_DESCRIPTOR_UUID)
                    characteristic.getDescriptor(cccDescriptorUUID).let { cccDescriptor ->
                        if (!gatt.setCharacteristicNotification(characteristic, false)) {
                            Log.e("Notification", "Error descriptor")
                            endOfOperation()
                            return
                        }
                        cccDescriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(cccDescriptor)
                        endOfOperation()
                    }
                }
            }
            else -> {
                endOfOperation()
                return
            }
        }
    }

    private fun BluetoothGattCharacteristic.isNotifiable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_NOTIFY)

    private fun BluetoothGattCharacteristic.isIndictable(): Boolean =
        containsProperty(BluetoothGattCharacteristic.PROPERTY_INDICATE)

    private fun BluetoothGattCharacteristic.containsProperty(property: Int): Boolean {
        return properties and property != 0
    }

    companion object {
        const val SCAN_ERROR = "SCAN ERROR"
        const val USER_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb"
        const val USER_UUID_STM = "00002a37-0000-1000-8000-00805f9b34fb"
        const val USER_UUID_TEXAS = "0000fff4-0000-1000-8000-00805f9b34fb"
    }
}
