package com.sooyoungjang.debuglibrary.util

import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sooyoungjang.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_LOG_FILTER_KEYWORD

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

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun putFilterKeyword(keyword: String) {
        val keywords = getFilterKeywordList().toMutableList()
        if (!keywords.contains(keyword)) {
            keywords.add(0, keyword)

            val result = GsonBuilder().create().toJson(
                keywords,
                object : TypeToken<ArrayList<String>>() {}.type
            )

            sharedPreferences.edit().apply {
                putString(EDDY_LOG_FILTER_KEYWORD, result)
                apply()
            }
        } else {
            return
        }
    }

    fun deleteFilterKeyword(keyword: String) {
        val keywords = getFilterKeywordList().toMutableList().also { it.remove(keyword) }

        val result = GsonBuilder().create().toJson(
            keywords,
            object : TypeToken<ArrayList<String>>() {}.type
        )
        sharedPreferences.edit().apply {
            putString(EDDY_LOG_FILTER_KEYWORD, result)
            apply()
        }
    }

    fun getTextSizePosition(): Int {
        return sharedPreferences.getInt(Constants.SharedPreferences.EDDY_LOG_TEXT_SIZE, 10)
    }

    fun getFilterKeywordList(): List<String> {
        val stringPrefs = sharedPreferences.getString(EDDY_LOG_FILTER_KEYWORD, "["+"normal"+"]")
        if (stringPrefs != null && stringPrefs != "[]") {
            return GsonBuilder().create().fromJson(stringPrefs, object : TypeToken<ArrayList<String>>() {}.type)
        }
        return emptyList()
    }

}