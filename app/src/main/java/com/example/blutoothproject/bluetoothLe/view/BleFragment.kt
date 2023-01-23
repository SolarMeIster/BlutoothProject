package com.example.blutoothproject.bluetoothLe.view

import android.content.Context
import android.os.*
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.blutoothproject.*
import com.example.blutoothproject.bluetoothLe.viewmodel.BleFragmentViewModel
import com.example.blutoothproject.bluetoothLe.viewmodel.ViewModelFactory
import com.example.blutoothproject.databinding.FragmentMainBinding
import kotlin.properties.Delegates

class BleFragment : Fragment() {

    private val bleFragmentViewModel: BleFragmentViewModel by viewModels {
        ViewModelFactory(
            requireContext().applicationContext as App
        )
    }

    private lateinit var binding: FragmentMainBinding
    private var characteristicValue = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bleFragmentViewModel.characteristicValue.observe(viewLifecycleOwner) {
            binding.txFirstSensor.text = it.toString()
        }
        binding = FragmentMainBinding.inflate(inflater, container, false)
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

        if (Build.VERSION_CODES.S <= Build.VERSION.SDK_INT && bleFragmentViewModel.exceedPressure()) {
            vibrator.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            )
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