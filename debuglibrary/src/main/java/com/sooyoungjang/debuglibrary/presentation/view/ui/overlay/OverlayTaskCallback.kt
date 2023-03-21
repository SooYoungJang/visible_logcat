package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

internal interface OverlayTaskCallback {
    val onClickOpen: () -> Unit
    val onClickClose: () -> Unit
    val onClickTagItem: (tag: String) -> Unit
    val onLongClickCloseService: () -> Unit
    val onClickDelete: () -> Unit
}