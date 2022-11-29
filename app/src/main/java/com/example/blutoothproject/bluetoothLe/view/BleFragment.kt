package com.example.blutoothproject.bluetoothLe.view

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.blutoothproject.*
import com.example.blutoothproject.databinding.FragmentMainBinding
import com.example.blutoothproject.delete.RandDataSocket
import com.example.blutoothproject.settings.view.SettingsFragment
import java.util.*

class BleFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    private lateinit var listPressure: MutableList<Int>

    private lateinit var timer: Timer

    private val randDataSocket: RandDataSocket by lazy {
        RandDataSocket()
    }
    private lateinit var list: MutableList<TextView>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            list = mutableListOf(txFirstSensor, txSecondSensor, txThirdSensor, txFourthSensor)

            toggleButton.setOnClickListener {
                if (toggleButton.text.toString() == "ON") {
                    sendPressure()
                } else timer.cancel()

            }
            menuToolBar.addMenuProvider(object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) { }

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
                                replace<ListBleDevicesFragment>(R.id.container)
                            }
                        }
                        else -> {}
                    }
                    return false
                }
            })
        }
    }

    // псевдо данные, которые получаем с контроллера
    private fun sendPressure() {
        timer = Timer("Pressure")
        timer.scheduleAtFixedRate(RandTimerTask(), DELAY_TIMER, PERIOD_TIMER)
    }

    inner class RandTimerTask : TimerTask() {
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
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.content_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.contentSettings -> {
                parentFragmentManager.commit {
                    setReorderingAllowed(true)
                    addToBackStack(null)
                    replace<SettingsFragment>(R.id.container)
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }*/
}