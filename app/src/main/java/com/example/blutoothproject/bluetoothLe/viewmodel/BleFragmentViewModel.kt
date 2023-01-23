package com.example.blutoothproject.bluetoothLe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blutoothproject.IObserver
import com.example.blutoothproject.allValueOfSettings
import com.example.blutoothproject.bluetoothLe.model.BleModel
import com.example.blutoothproject.settings.view.SettingsFragment

class BleFragmentViewModel(private val bleModel: BleModel) : ViewModel(), IObserver {

    private val _characteristicValue = MutableLiveData<Int>()
    val characteristicValue: LiveData<Int> = _characteristicValue

    init {
        bleModel.addListener(this)
    }

    override fun update() {
        _characteristicValue.postValue(bleModel.characteristicValue)
    }

    fun exceedPressure(): Boolean {
        return (allValueOfSettings[SettingsFragment.MAX] as Int) < _characteristicValue.value!!
    }

    override fun onCleared() {
        bleModel.removeListener(this)
    }
}