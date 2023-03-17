package com.eddy.debuglibrary.util

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
        return resources.displayMetrics.widthPixels
    }

    override fun getScreenHeight(): Int {
        return resources.displayMetrics.heightPixels
    }

    override fun getString(@StringRes resId: Int): String {
        return resources.getString(resId)
    }

    override fun getString(resId: Int, vararg args: Any): String {
        return resources.getString(resId, *args)
    }

    override fun getStatusHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
    }

    override fun getDrawable(resId: Int, theme: Resources.Theme?): Drawable {
        return resources.getDrawable(resId, theme)
    }

    override fun getStringList(resId: Int): List<String> {
        return resources.getStringArray(resId).toList()
    }

}