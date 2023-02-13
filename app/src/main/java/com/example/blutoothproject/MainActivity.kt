package com.example.blutoothproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.blutoothproject.bluetoothLe.listbledevices.view.ListBleDevicesFragment
import com.example.blutoothproject.databinding.ActivityMainBinding
import com.example.blutoothproject.settings.model.IOSettings


class MainActivity : AppCompatActivity(){

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val config = IOSettings()

        // сохранение настроек
        val prefs = getPreferences(MODE_PRIVATE)
        if (prefs.getBoolean("firstRun", true)){
            prefs.edit().putBoolean("firstRun", false).apply()
            config.saveDate(this, allValueOfSettings)
        } else {
            allValueOfSettings = config.getData(this)
        }

        // запуск фрагмента
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<ListBleDevicesFragment>(R.id.container)
            }
        }
    }
}