package com.eddy.debuglibrary.presentation.view.ui.overlay.epoxy

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewParent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.eddy.debuglibrary.presentation.base.KotlinEpoxyHolder
import com.eddy.debuglibrary.util.Constants
import com.example.debuglibrary.R

@EpoxyModelClass
internal abstract class LogItemView : EpoxyModelWithHolder<LogItemViewHolder>() {

    override fun getDefaultLayout(): Int = R.layout.epoxy_log_item_view

    @EpoxyAttribute
    lateinit var content: String

    @EpoxyAttribute
    var contentColor: Int = 0

    override fun bind(holder: LogItemViewHolder) {
        with(holder) {
            textView.apply {
                text = content
                setTextColor(ContextCompat.getColor(context, contentColor))
                textSize = textList[sharedPreferences.getInt(Constants.SharedPreferences.EDDY_LOG_TEXT_SIZE, 10)].toFloat()
            }
        }
    }
}

internal class LogItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<LinearLayout>(R.id.root)
    val textView by bind<TextView>(R.id.tv_content)
    val sharedPreferences: SharedPreferences = (parent as View).context.getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)
    val textList: Array<String> = (parent as View).context.resources.getStringArray(R.array.text_size_array)

}