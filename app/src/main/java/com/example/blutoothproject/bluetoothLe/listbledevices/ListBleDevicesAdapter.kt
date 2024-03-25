package com.example.blutoothproject.bluetoothLe.listbledevices

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blutoothproject.bluetoothLe.DiffCallback
import com.example.blutoothproject.databinding.ItemBleDeviceBinding

@SuppressLint("MissingPermission")
class ListBleDevicesAdapter(private val onClickListener: ((result: ScanResult) -> Unit)) :
    RecyclerView.Adapter<ListBleDevicesAdapter.ViewHolder>() {

    private lateinit var binding: ItemBleDeviceBinding

    var data = emptyList<ScanResult>()
        set(newField) {
            val diffCallback = DiffCallback(field, newField)
            val listBleDeviceDiffResult = DiffUtil.calculateDiff(diffCallback)
            field = newField
            listBleDeviceDiffResult.dispatchUpdatesTo(this)
        }

    class ViewHolder(
        private val binding: ItemBleDeviceBinding,
        private val onClickListener: (result: ScanResult) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ScanResult) {
            with(binding) {
                txDeviceName.text = data.device.name ?: "Unnamed"
                txMacAddress.text = data.device.address
                itemBleDevice.setOnClickListener { onClickListener.invoke(data) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemBleDeviceBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = data[position]
        holder.bind(info)
    }

    override fun getItemCount(): Int {
        return data.size
    }

}