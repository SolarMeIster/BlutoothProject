package com.example.blutoothproject.bluetoothLe.bledata

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blutoothproject.BleStruct
import com.example.blutoothproject.bluetoothLe.bledata.viewmodel.BleDataViewModel
import com.example.blutoothproject.databinding.ItemDataRecordBinding

@SuppressWarnings("MissingPermission")
class BleDataAdapter(private val clickListener: (() -> Unit)) :
    RecyclerView.Adapter<BleDataAdapter.ViewHolder>() {

    private lateinit var binding: ItemDataRecordBinding

    var data = emptyList<Pair<BluetoothDevice, BleStruct>>()
        set(newField) {
            field = newField
            notifyDataSetChanged()
        }

    class ViewHolder(
        private val binding: ItemDataRecordBinding,
        private val clickListener: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<BluetoothDevice, BleStruct>) {

            with(binding) {
                txDeviceNameData.text = data.first.name
                //spinner.onItemClickListener {clickListener.invoke()}
                when (data.second.flags) {
                    1 -> {
                        dataPressure.text = data.second.pressure.toString()
                        if (dataPressure.visibility != View.VISIBLE) {
                            visiblePressure()
                        }
                    }
                    2 -> {
                        dataPressure.text = data.second.pressure.toString()
                        dataTemp.text = data.second.temperature.toString()
                        if (
                            dataPressure.visibility != View.VISIBLE &&
                            dataTemp.visibility != View.VISIBLE
                        ) {
                            visiblePressure()
                            visibleTemp()
                        }
                    }
                    3 -> {
                        dataPressure.text = data.second.pressure.toString()
                        dataTemp.text = data.second.temperature.toString()
                        dataHumidity.text = data.second.humidity.toString()
                        if (
                            dataPressure.visibility != View.VISIBLE &&
                            dataTemp.visibility != View.VISIBLE &&
                            dataHumidity.visibility != View.VISIBLE
                        ) {
                            visiblePressure()
                            visibleTemp()
                            visibleHum()
                        }
                    }
                    101 -> {
                        dataTemp.text = data.second.flags.toString()
                        visibleTemp()
                    }
                    else -> {

                    }
                }
            }
        }

        private fun visiblePressure() {
            with(binding) {
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
        return ViewHolder(binding, clickListener)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = data[position]
        holder.bind(info)
    }
}