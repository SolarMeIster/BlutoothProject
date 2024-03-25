package com.example.blutoothproject.bluetoothLe

import android.bluetooth.le.ScanResult
import androidx.recyclerview.widget.DiffUtil

class DiffCallback (private val oldList: List<ScanResult>, private val newList: List<ScanResult>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].device.address == newList[newItemPosition].device.address
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}