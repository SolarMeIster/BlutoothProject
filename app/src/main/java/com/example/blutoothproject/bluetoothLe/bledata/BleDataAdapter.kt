package com.example.blutoothproject.bluetoothLe.bledata

import android.bluetooth.BluetoothGatt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blutoothproject.BleStruct
import com.example.blutoothproject.bluetoothLe.model.BleModel
import com.example.blutoothproject.databinding.ItemDataRecordBinding

@SuppressWarnings("MissingPermission")

class BleDataAdapter :
    RecyclerView.Adapter<BleDataAdapter.ViewHolder>() {

    private lateinit var binding: ItemDataRecordBinding

    var data = emptyList<
            Pair<BluetoothGatt, BleStruct>
            >()
        set(newField) {
            field = newField
            notifyDataSetChanged()
        }

    class ViewHolder(
        private val binding: ItemDataRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(currentData: Pair<BluetoothGatt, BleStruct>, countOfDevices: Int) {
            val deviceName = currentData.first.device.name
            val round: String
            with(binding) {
                
                txDeviceNameData.text = deviceName
                //spinner.onItemClickListener {clickListener.invoke()}
                if (deviceName == BleModel.EXTERNAL_SENSOR || countOfDevices <= 1) {
                    txDimPressure.text = " мм. рт. ст."
                    round = "%.2f"
                } else {
                    txDimPressure.text = " мбар"
                    round = "%.1f"
                }
                when (currentData.second.flags) {
                    1 -> {
                        dataPressure.text = String.format(round, currentData.second.pressure)
                        if (dataPressure.visibility != View.VISIBLE) {
                            visiblePressure()
                        }
                    }
                    2 -> {
                        dataPressure.text = String.format(round, currentData.second.pressure)
                        dataTemp.text = currentData.second.temperature.toString()
                        if (
                            dataPressure.visibility != View.VISIBLE &&
                            dataTemp.visibility != View.VISIBLE
                        ) {
                            visiblePressure()
                            visibleTemp()
                        }
                    }
                    3 -> {
                        dataPressure.text = String.format(round, currentData.second.pressure)
                        dataTemp.text = currentData.second.temperature.toString()
                        dataHumidity.text = currentData.second.humidity.toString()
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
                        dataTemp.text = currentData.second.flags.toString()
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
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = data[position]
        holder.bind(info, data.size)
    }
}