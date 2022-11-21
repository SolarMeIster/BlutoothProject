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
import com.example.blutoothproject.R
import com.example.blutoothproject.allValueOfSettings
import com.example.blutoothproject.databinding.FragmentSettingsBinding
import com.example.blutoothproject.settings.model.IOSettings

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    private lateinit var btnDialogAccept: Button
    private lateinit var btnDialogCancel: Button
    private lateinit var editText: EditText

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
        val myDialogFragment = LayoutInflater.from(requireActivity()).inflate(R.layout.dialog_fragment_setting, null)
        val myBuilder = AlertDialog.Builder(requireActivity())
            .setView(myDialogFragment)
        val myAlertDialog = myBuilder.show()
        btnDialogAccept = myDialogFragment.findViewById(R.id.btnDialogAccept)
        btnDialogCancel = myDialogFragment.findViewById(R.id.btnDialogCancel)
        editText = myDialogFragment.findViewById(R.id.editText)
        btnDialogAccept.setOnClickListener {
            val valuePressure = editText.text.toString()
            myAlertDialog.dismiss()
            textView.text = valuePressure
            val config = IOSettings()
            config.saveData(requireContext(), valuePressure, key)
            allValueOfSettings = config.getData(requireContext())
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
