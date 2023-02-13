package com.example.blutoothproject.bluetoothLe

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic

// класс, отвечающий за типы BLE операций
sealed class BleOperationType {
    abstract val device: BluetoothDevice
}

// подключение телефона к ble устройству
data class Connect(override val device: BluetoothDevice) : BleOperationType()

// отключение телефона от ble устройства
data class Disconnect(override val device: BluetoothDevice) : BleOperationType()

// разрешение на получение уведомлений от ble устройства
data class EnableNotification(override val device: BluetoothDevice, val characteristic: BluetoothGattCharacteristic) : BleOperationType()

// отключение уведомлений от ble устройства
data class DisableNotification(override val device: BluetoothDevice, val characteristic: BluetoothGattCharacteristic) : BleOperationType()
