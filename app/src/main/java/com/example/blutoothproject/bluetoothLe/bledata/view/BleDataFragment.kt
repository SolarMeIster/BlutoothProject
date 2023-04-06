package com.example.blutoothproject.bluetoothLe.bledata.view

import android.content.Context
import android.os.*
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blutoothproject.*
import com.example.blutoothproject.bluetoothLe.bledata.BleDataAdapter
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
            toolbarData.addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                   when(menuItem.itemId) {
                       R.id.ledCheck -> {
                           bleDataViewModel.writeChar()
                       }
                       R.id.bluetoothDisconnect -> {

                       }
                   }
                    return true
                }

            })
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