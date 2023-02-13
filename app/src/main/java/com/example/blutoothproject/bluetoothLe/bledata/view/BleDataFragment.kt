package com.example.blutoothproject.bluetoothLe.bledata.view

import android.content.Context
import android.os.*
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blutoothproject.*
import com.example.blutoothproject.bluetoothLe.BleDataAdapter
import com.example.blutoothproject.ViewModelFactory
import com.example.blutoothproject.bluetoothLe.bledata.viewmodel.BleDataViewModel
import com.example.blutoothproject.databinding.FragmentBleDataBinding

class BleDataFragment : Fragment() {

    private val bleDataViewModel: BleDataViewModel by viewModels {
        ViewModelFactory(
            requireContext().applicationContext as App
        )
    }

    private val bleDataAdapter: BleDataAdapter by lazy {
        BleDataAdapter()
    }

    private lateinit var binding: FragmentBleDataBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bleDataViewModel.characteristicValue.observe(viewLifecycleOwner) { result ->
            bleDataAdapter.data = result.toList()
        }
        binding = FragmentBleDataBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val vibrator: VibratorManager by lazy {
            requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        }
        with(binding) {
            toolbarData.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        /*if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT && bleDataFragmentViewModel.exceedPressure()) {
            vibrator.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            )
        }*/
    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewOfBleDeviceData.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewOfBleDeviceData.adapter = bleDataAdapter
        }
    }


}

/*// псевдо данные, которые получаем с контроллера
private fun sendPressure() {
    timer = Timer("Pressure")
    timer.scheduleAtFixedRate(RandTimerTask(), DELAY_TIMER, PERIOD_TIMER)
}*/

/* inner class RandTimerTask : TimerTask() {
     override fun run() {
         listPressure = randDataSocket.getPressure()
         activity?.runOnUiThread {
             for (i in list.indices) {
                 if (listPressure[i] !in allValueOfSettings[SettingsFragment.MIN].toString()
                         .toInt()
                     ..allValueOfSettings[SettingsFragment.MAX].toString().toInt()
                 ) {
                     Toast.makeText(
                         requireContext(),
                         "Превышен предел давления",
                         Toast.LENGTH_SHORT
                     ).show()
                 }
                 list[i].text = listPressure[i].toString()
             }
         }
     }
 }*/