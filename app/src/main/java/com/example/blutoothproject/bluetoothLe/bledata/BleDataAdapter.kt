package com.example.blutoothproject.bluetoothLe.bledata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blutoothproject.BleStruct
import com.example.blutoothproject.databinding.ItemDataRecordBinding

@SuppressWarnings("MissingPermission")
class BleDataAdapter : RecyclerView.Adapter<BleDataAdapter.ViewHolder>() {

    private lateinit var binding: ItemDataRecordBinding

    var data = emptyList<Pair<String, BleStruct>>()
        set(newField) {
            field = newField
            notifyDataSetChanged()
        }

    class ViewHolder(private val binding: ItemDataRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<String, BleStruct>) {
            with(binding) {
                when(data.second.flags) {
                    1 -> {
                        txDeviceNameData.text = data.first
                        dataPressure.text = data.second.pressure.toString()
                        visiblePressure()
                    }
                    2 -> {
                        txDeviceNameData.text = data.first
                        dataPressure.text = data.second.pressure.toString()
                        dataTemp.text = data.second.temperature.toString()
                        visiblePressure()
                        visibleTemp()
                    }
                    3 -> {
                        txDeviceNameData.text = data.first
                        dataPressure.text = data.second.pressure.toString()
                        dataTemp.text = data.second.temperature.toString()
                        dataHumidity.text = data.second.humidity.toString()
                        visiblePressure()
                        visibleTemp()
                        visibleHum()
                    }
                }
            }
        }

        private fun visiblePressure() {
            with(binding){
                dataPressure.visibility = View.VISIBLE
                txPressure.visibility = View.VISIBLE
                txDimPressure.visibility = View.VISIBLE
            }
        }

        private fun visibleTemp() {
            with(binding) {
                dataTemp.visibility = View.VISIBLE
                txTemp.visibility = View.VISIBLE
                txDimTemp.visibility = View.VISIBLE
            }
        }

        private fun visibleHum() {
            with(binding) {
                dataHumidity.visibility = View.VISIBLE
                txHumidity.visibility = View.VISIBLE
                txDimHum.visibility = View.VISIBLE
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