package com.sooyoungjang.debuglibrary.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface ResourceProvider {

    fun getDimensions(resId: Int): Float

    fun getScreenWidth(): Int

    fun getScreenHeight(): Int

    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg args: Any): String

    fun getDrawable(resId: Int, theme: Resources.Theme? = null): Drawable

    fun getStringList(resId: Int): List<String>

}