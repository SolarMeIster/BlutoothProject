package com.example.blutoothproject.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.settings.model.IOSettings

class SettingViewModel(private val config: IOSettings) : ViewModel() {

    fun saveData(context: Context, value: Any, key: String) {
        config.saveData(context, value, key)
    }

    fun getData(context: Context): Map<String, *> {
        return config.getData(context)
    }

}