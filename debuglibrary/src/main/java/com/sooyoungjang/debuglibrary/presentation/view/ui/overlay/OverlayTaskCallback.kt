package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel

internal interface OverlayTaskCallback {
    val onClickOpen: () -> Unit
    val onClickClose: () -> Unit
    val onClickTagItem: (tag: String) -> Unit
    val onClickBackPressed: (tag: String) -> Unit
    val onLongClickCloseService: () -> Unit
    val onClickDelete: () -> Unit
    val onClickSearch: (searchKeyword: String ,logUiModels: List<LogUiModel>?) -> Unit
    val onClickPageUp: (logUiModels: List<LogUiModel>?, currentPosition: Int) -> Unit
    val onClickPageDown: (logUiModels: List<LogUiModel>?, currentPosition: Int) -> Unit
}