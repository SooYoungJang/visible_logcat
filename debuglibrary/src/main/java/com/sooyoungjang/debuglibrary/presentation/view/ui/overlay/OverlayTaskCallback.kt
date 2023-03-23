package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel

internal interface OverlayTaskCallback {
    val onClickOpen: () -> Unit
    val onClickClose: () -> Unit
    val onClickTagItem: (position: Int) -> Unit
    val onCollectLog: (keyword: String) -> Unit
    val onClickBackPressed: () -> Unit
    val onLongClickCloseService: () -> Unit
    val onClickDelete: () -> Unit
    val onClickSearch: (logUiModels: List<LogUiModel>?, keyword: String) -> Unit
    val onClickPageUp: (logUiModels: List<LogUiModel>?, keyword: String, currentPosition: Int) -> Unit
    val onClickPageDown: (logUiModels: List<LogUiModel>?, keyword: String, currentPosition: Int) -> Unit
}