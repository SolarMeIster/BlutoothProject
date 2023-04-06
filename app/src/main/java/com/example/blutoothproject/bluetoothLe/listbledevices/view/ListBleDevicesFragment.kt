package com.example.blutoothproject.bluetoothLe.listbledevices.view

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blutoothproject.databinding.FragmentListBleDevicesBinding
import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.example.blutoothproject.App
import com.example.blutoothproject.R
import com.example.blutoothproject.bluetoothLe.listbledevices.ListBleDevicesAdapter
import com.example.blutoothproject.bluetoothLe.listbledevices.viewmodel.ListBleDevicesViewModel
import com.example.blutoothproject.bluetoothLe.bledata.view.BleDataFragment
import com.example.blutoothproject.ViewModelFactory
import com.example.blutoothproject.settings.view.SettingsFragment

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

    private val requestPermissionGranted =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) startBleScan()
        }

    private var isScanning = false
        set(value) {
            field = value
            if (value)
                binding.btnScanBleDevices.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireActivity(),
                        R.drawable.ic_bluetooth_search
                    )
                )
            else
                binding.btnScanBleDevices.setImageDrawable(
                    AppCompatResources.getDrawable(requireActivity(), R.drawable.ic_bluetooth)
                )
        }

    private val listBleDevicesAdapter: ListBleDevicesAdapter by lazy {
        ListBleDevicesAdapter { result: ScanResult ->
            isScanning = false
            with(result) {
                listBleDevicesViewModel.connect(device)
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    addToBackStack(null)
                    replace<BleDataFragment>(R.id.container)
                }
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
        // нажатие на кнопку
        binding.btnScanBleDevices.setOnClickListener {
            if (!listBleDevicesViewModel.checkBluetooth()) {
                checkBluetooth()
                return@setOnClickListener
            }
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
        with(binding.menuToolBar) {
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.contentSettings -> {
                            parentFragmentManager.commit {
                                setReorderingAllowed(true)
                                addToBackStack(null)
                                replace<SettingsFragment>(R.id.container)
                            }
                        }
                        R.id.contentBleScan -> {
                            parentFragmentManager.commit {
                                setReorderingAllowed(true)
                                addToBackStack(null)
                                replace<BleDataFragment>(R.id.container)
                            }
                        }
                        else -> throw IllegalStateException("Error")
                    }
                    return false
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
        if (!listBleDevicesViewModel.checkBluetooth()) {
            checkBluetooth()
        }
    }

    // старт сканирования
    private fun startBleScan() {
        if (!requireActivity().hasRequiredRunTimePermission()) {
            requestRelevantRuntimePermissions()
        }
        // Прошлый Вова: добавить корутины
        // Сейчашний Вова: а все асинхронка уже есть!!!
        else {

            listBleDevicesViewModel.startSearch()
            isScanning = true
        }
    }

    // остоновка сканирования
    private fun stopBleScan() {
        listBleDevicesViewModel.stopSearch()
        isScanning = false
    }

    // проверяет версию Android и запрашивает необходимые permissions
    private fun requestRelevantRuntimePermissions() {
        if (requireActivity().hasRequiredRunTimePermission()) {
            return
        }
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                // доступ к местоположению
                requestPermissionGranted.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                // доступ к сканированию и подключению Bluetooth
                requestPermissionGranted.launch(
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
                )
            }
        }
    }

    // проверка разрешений
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

    // запуск Bluetooth, если на телефоне он выключен
    private fun checkBluetooth() {
        val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startForResult.launch(enableBluetooth)
    }

    companion object {
        const val SCAN_PERIOD = 30000L
    }
}