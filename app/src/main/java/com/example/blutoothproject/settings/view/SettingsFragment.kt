package com.example.blutoothproject.settings.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.blutoothproject.App
import com.example.blutoothproject.R
import com.example.blutoothproject.ViewModelFactory
import com.example.blutoothproject.allValueOfSettings
import com.example.blutoothproject.databinding.FragmentSettingsBinding
import com.example.blutoothproject.settings.viewmodel.SettingViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private val settingViewModel: SettingViewModel by viewModels {
        ViewModelFactory(
            requireContext().applicationContext as App
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            toolbarSettings.setNavigationOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            maxValue.text = allValueOfSettings[MAX].toString()
            minValue.text = allValueOfSettings[MIN].toString()

            maxValue.setOnClickListener {
                performDialog(maxValue, MAX)
            }
            maxValue.setOnClickListener {
                performDialog(maxValue, MIN)
            }
        }

    }

    // выполнение DialogFragment
    private fun performDialog(textView: TextView, key: String) {
        val myDialogFragment =
            LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_fragment_setting, null)
        val myBuilder = AlertDialog.Builder(requireActivity())
            .setView(myDialogFragment)
        val myAlertDialog = myBuilder.show()
        val btnDialogAccept = myDialogFragment.findViewById<Button>(R.id.btnDialogAccept)
        val btnDialogCancel = myDialogFragment.findViewById<Button>(R.id.btnDialogCancel)
        val editText = myDialogFragment.findViewById<EditText>(R.id.editText)
        btnDialogAccept.setOnClickListener {
            val valuePressure = editText.text.toString()
            myAlertDialog.dismiss()
            textView.text = valuePressure
            settingViewModel.saveData(requireContext(), valuePressure, key)
            allValueOfSettings = settingViewModel.getData(requireContext())
        }
        btnDialogCancel.setOnClickListener {
            myAlertDialog.dismiss()
        }
    }

    companion object {
        const val MAX = "maxValueOfPressure"
        const val MIN = "minValueOfPressure"
    }
}

