package com.example.blutoothproject.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.blutoothproject.R

class SettingDialogFragment : DialogFragment() {

    private lateinit var editText: EditText
    private lateinit var btnDialogAccept: Button
    private lateinit var btnDialogCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_setting, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnDialogAccept = view.findViewById(R.id.btnDialogAccept)
        btnDialogCancel = view.findViewById(R.id.btnDialogCancel)

        btnDialogAccept.setOnClickListener {
            editText = view.findViewById(R.id.editText)
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

    companion object {
        const val SETTING_FOR_PRESSURE = "PRESSURE"
    }

}