package com.sooyoungjang.debuglibrary.data.datastore.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.sooyoungjang.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL_BOOLEAN_TOKEN
import com.sooyoungjang.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL_INT_TOKEN
import com.sooyoungjang.debuglibrary.util.Constants.SharedPreferences.Companion.EDDY_DEBUG_TOOL_STRING_TOKEN

internal class DataStoreLocalDataSourceImpl(
    private val pref: SharedPreferences
) : DataStoreLocalDataSource {

    override fun readNeverSeeAgain(): Boolean {
        return pref.getBoolean(EDDY_DEBUG_TOOL_BOOLEAN_TOKEN, false)
    }

    override suspend fun <T> write(param: T) {
        when(param) {
            is String -> { pref.edit { putString(EDDY_DEBUG_TOOL_STRING_TOKEN, param) } }
            is Int -> { pref.edit { putInt(EDDY_DEBUG_TOOL_INT_TOKEN, param) } }
            is Boolean -> { pref.edit { putBoolean(EDDY_DEBUG_TOOL_BOOLEAN_TOKEN, param) } }
        }
    }

    override suspend fun <T> clear(param: T) {
        when(param) {
            is String -> { pref.edit { remove(EDDY_DEBUG_TOOL_STRING_TOKEN) } }
            is Int -> { pref.edit { remove(EDDY_DEBUG_TOOL_INT_TOKEN) } }
            is Boolean -> { pref.edit { remove(EDDY_DEBUG_TOOL_BOOLEAN_TOKEN) } }
        }
    }


}