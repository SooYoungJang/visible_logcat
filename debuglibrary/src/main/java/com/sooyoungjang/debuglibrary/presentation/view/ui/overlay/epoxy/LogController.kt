package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel

internal class LogController: TypedEpoxyController<List<LogUiModel>>() {
    override fun buildModels(data: List<LogUiModel>?) {
        data?.forEachIndexed { index, itemData ->
            logItemView {
                id(modelCountBuiltSoFar)
                content(itemData.content)
                contentColor(itemData.contentColor)
            }
        }
    }
}