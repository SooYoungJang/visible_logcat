package com.eddy.debuglibrary.presentation.view.ui.setting.epoxy

import com.airbnb.epoxy.TypedEpoxyController

internal class LogKeywordController : TypedEpoxyController<List<LogKeywordModel>>() {
    override fun buildModels(datas: List<LogKeywordModel>?) {
        datas?.forEachIndexed { index, data ->
            logKeywordItemView {
                id(modelCountBuiltSoFar)
                content(data.content)
                onClickTrash { data.callback.onClickDeleteKeyword.invoke(data.content) }
            }
        }
    }
}