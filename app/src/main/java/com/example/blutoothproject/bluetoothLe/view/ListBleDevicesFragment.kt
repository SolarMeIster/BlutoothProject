package com.example.blutoothproject.bluetoothLe.view

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blutoothproject.bluetoothLe.viewmodel.BleViewModel
import com.example.blutoothproject.databinding.FragmentListBleDevicesBinding

class ListBleDevicesFragment : Fragment() {

    private lateinit var binding: FragmentListBleDevicesBinding

    private val bleViewModel: BleViewModel by activityViewModels()

    private val scanResultList = mutableListOf<ScanResult>()

    private val listBleDevices: ListBleDevicesRecyclerAdapter by lazy {
        ListBleDevicesRecyclerAdapter(scanResultList, { result: ScanResult ->

        })
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBleDevicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewOfBleDevices.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewOfBleDevices.adapter = listBleDevices
        }

    }

}