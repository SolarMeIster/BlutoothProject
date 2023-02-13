package com.example.blutoothproject.bluetoothLe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blutoothproject.databinding.ItemDataRecordBinding

@SuppressWarnings("MissingPermission")
class BleDataAdapter : RecyclerView.Adapter<BleDataAdapter.ViewHolder>() {

    private lateinit var binding: ItemDataRecordBinding

    var data = emptyList<Pair<String, Int>>()
        set(newField) {
            field = newField
            notifyDataSetChanged()
        }

    class ViewHolder(private val binding: ItemDataRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<String, Int>) {
            with(binding) {
                txDeviceNameData.text = data.first
                txData.text = data.second.toString()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemDataRecordBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = data[position]
        holder.bind(info)
    }
}