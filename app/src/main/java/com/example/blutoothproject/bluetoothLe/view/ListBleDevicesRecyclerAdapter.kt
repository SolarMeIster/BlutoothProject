package com.example.blutoothproject.bluetoothLe.view

import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.blutoothproject.R

class ListBleDevicesRecyclerAdapter(private val data: List<ScanResult>, private val onClickListener: ((result: ScanResult) -> Unit)) :
    RecyclerView.Adapter<ListBleDevicesRecyclerAdapter.ViewHolder>() {

    class ViewHolder(private val view: View, private val onClickListener: (result: ScanResult) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bind(data: ScanResult) {
            val deviceName = view.findViewById<TextView>(R.id.txDeviceName)
            val macAddress = view.findViewById<TextView>(R.id.txMacAddress)
            deviceName.text = data.device.name
            macAddress.text = data.device.address
            view.setOnClickListener { onClickListener.invoke(data) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ble_device, parent, false)
        return ViewHolder(view, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = data[position]
        holder.bind(info)
    }

    override fun getItemCount(): Int = data.size
}