package com.eddy.debuglibrary.util

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SharedPreferencesUtil(
    private val sharedPreferences: SharedPreferences,
) {

    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getTextSizePosition(): Int {
        return sharedPreferences.getInt(Constants.SharedPreferences.EDDY_LOG_TEXT_SIZE, 10)
    }

    fun getFilterKeywordList(): List<String> {
        val stringPrefs = sharedPreferences.getString(Constants.SharedPreferences.EDDY_LOG_FILTER_KEYWORD, null)

        if (stringPrefs != null && stringPrefs != "[]") {
            return GsonBuilder().create().fromJson(stringPrefs, object : TypeToken<ArrayList<String>>() {}.type)
        }
        return emptyList()
    }

}