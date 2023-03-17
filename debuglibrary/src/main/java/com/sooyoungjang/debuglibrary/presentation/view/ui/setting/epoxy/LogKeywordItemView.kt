package com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy

import android.view.ViewParent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.sooyoungjang.debuglibrary.presentation.base.KotlinEpoxyHolder
import com.example.debuglibrary.R

@EpoxyModelClass
internal abstract class LogKeywordItemView : EpoxyModelWithHolder<LogFilterKeywordItemViewHolder>() {
    override fun getDefaultLayout(): Int = R.layout.epoxy_log_filter_keyword_item_view

    @EpoxyAttribute
    lateinit var content: String

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickTrash: (() -> Unit)? = null

    override fun bind(holder: LogFilterKeywordItemViewHolder) {
        with(holder) {
            trashView.isVisible = content != "normal"
            textView.apply {
                text = content
            }

            trashView.setOnClickListener {
                onClickTrash?.invoke()
            }
        }
    }
}

internal class LogFilterKeywordItemViewHolder(parent: ViewParent) : KotlinEpoxyHolder() {
    val root by bind<LinearLayout>(R.id.root)
    val textView by bind<TextView>(R.id.tv_content)
    val trashView by bind<ImageView>(R.id.iv_trash_keyword)
}