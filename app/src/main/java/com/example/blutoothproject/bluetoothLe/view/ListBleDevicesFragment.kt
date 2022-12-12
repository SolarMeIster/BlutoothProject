package com.example.blutoothproject.bluetoothLe.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blutoothproject.bluetoothLe.viewmodel.ListBleDevicesViewModel
import com.example.blutoothproject.databinding.FragmentListBleDevicesBinding
import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.example.blutoothproject.App
import com.example.blutoothproject.R
import com.example.blutoothproject.bluetoothLe.ListBleDevicesAdapter
import com.example.blutoothproject.bluetoothLe.viewmodel.ViewModelFactory

class ListBleDevicesFragment : Fragment() {

    private lateinit var binding: FragmentListBleDevicesBinding

    private val listBleDevicesViewModel: ListBleDevicesViewModel by viewModels {
        ViewModelFactory(
            requireContext().applicationContext as App
        )
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) {
                checkBluetooth()
            }
        }

    private var isScanning = false
        set(value) {
            field = value
            if (value)
                binding.btnScanBleDevices.setImageDrawable(
                    requireContext().getDrawable(R.drawable.ic_bluetooth_search)
                )
            else
                binding.btnScanBleDevices.setImageDrawable(
                    requireContext().getDrawable(R.drawable.ic_bluetooth)
                )
        }

    private val listBleDevicesAdapter: ListBleDevicesAdapter by lazy {
        ListBleDevicesAdapter() { result: ScanResult ->
            isScanning = false
            with(result) {
                listBleDevicesViewModel.connect(device)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBleDevicesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnScanBleDevices.setOnClickListener {
            if (isScanning)
                stopBleScan()
            else {
                Handler(Looper.getMainLooper()).postDelayed({ stopBleScan() }, SCAN_PERIOD)
                startBleScan()
            }
        }
        listBleDevicesViewModel.listDevice.observe(viewLifecycleOwner) {
            listBleDevicesAdapter.data = it
        }
    }


    override fun onResume() {
        super.onResume()
        if (!listBleDevicesViewModel.checkBluetooth()) {
            checkBluetooth()
        }
    }

    private fun startBleScan() {
        if (!requireActivity().hasRequiredRunTimePermission()) {
            requestRelevantRuntimePermissions()
        }
        // добавить корутины!!!
        else {
            listBleDevicesViewModel.startSearch()
            isScanning = true
        }
    }

    private fun stopBleScan() {
        listBleDevicesViewModel.stopSearch()
        isScanning = false
    }

    private fun requestRelevantRuntimePermissions() {
        if (requireActivity().hasRequiredRunTimePermission()) {
            return
        }
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                requiredLocationPermission()
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                requiredBluetoothPermission()
            }
        }
    }

    // доступ к местоположению
    private fun requiredLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RUNTIME_PERMISSION_REQUEST_CODE
        )
    }

    // доступ к сканированию и подключению Bluetooth
    private fun requiredBluetoothPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT),
            RUNTIME_PERMISSION_REQUEST_CODE
        )
    }

    // проверка permissions
    private fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    // взависимости от версии Android, смотрит какие доступы приложению нужны
    private fun Context.hasRequiredRunTimePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // запуск RecyclerView
    private fun setupRecyclerView() {
        with(binding) {
            recyclerViewOfBleDevices.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewOfBleDevices.adapter = listBleDevicesAdapter
        }
    }

    // запуск Bluetooth
    private fun checkBluetooth() {
        val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startForResult.launch(enableBluetooth)
    }

    companion object {
        const val SCAN_PERIOD = 10000L
        const val RUNTIME_PERMISSION_REQUEST_CODE = 2
    }
}