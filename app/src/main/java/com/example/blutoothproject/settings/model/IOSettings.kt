package com.example.blutoothproject.settings.model

import android.content.Context

class IOSettings {

    private var data: Map<String, *> = mapOf<String, Any>()
    private var flagLoad = true

    fun saveDate(context: Context, maps: Map<String, *>) {
        val setting = context.getSharedPreferences(STORE_DATE, Context.MODE_PRIVATE)
        val editor = setting.edit()
        for ((key, value) in maps) {
            if (value is Int){
                editor.putInt(key, value)
            }
            else if (value is String) {
                editor.putString(key, value)
            }
        }
        editor.apply()
    }

    fun saveData(context: Context, value: Any, key: String) {
        val setting = context.getSharedPreferences(STORE_DATE, Context.MODE_PRIVATE)
        val editor = setting.edit()
        when (value){
            is Int -> editor.putInt(key, value)
            is String -> editor.putString(key, value)
        }
        editor.apply()
    }

    private fun loadDate(context: Context): Map<String, *> {
        return context.getSharedPreferences(STORE_DATE, Context.MODE_PRIVATE).all
    }

    fun getData(context: Context, key: String): Any? {
        if (flagLoad){
            data = loadDate(context)
            flagLoad = false
        }
        return data[key]
    }

    fun getData(context: Context): Map<String, *> {
        if (flagLoad){
            data = loadDate(context)
            flagLoad = false
        }
        return data
    }

    companion object {
        const val STORE_DATE = "DATA"
    }
}