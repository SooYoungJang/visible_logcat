package com.sooyoungjang.debuglibrary.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.StringRes

class ResourceProviderImpl (
    private val resources: Resources,
) : ResourceProvider {

    override fun getDimensions(resId: Int): Float {
        return resources.getDimension(resId)
    }

    override fun getScreenWidth(): Int {
        return resources.configuration.screenWidthDp
    }

    override fun getScreenHeight(): Int {
        return resources.configuration.screenHeightDp
    }

    override fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    override fun getString(resId: Int, vararg args: Any): String {
        return resources.getString(resId, *args)
    }

    override fun getDrawable(resId: Int, theme: Resources.Theme?): Drawable {
        return resources.getDrawable(resId, theme)
    }

    override fun getStringList(resId: Int): List<String> {
        return resources.getStringArray(resId).toList()
    }

}