package com.example.blutoothproject.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.blutoothproject.databinding.DialogFragmentSettingBinding

class SettingDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            btnDialogAccept.setOnClickListener {
                val valuePressure = editText.text.toString()
                val bundle = Bundle()
                bundle.putString(SETTING_FOR_PRESSURE, valuePressure)
                SettingsFragment().arguments = bundle
                dismiss()
            }
            btnDialogCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val SETTING_FOR_PRESSURE = "PRESSURE"
    }

}