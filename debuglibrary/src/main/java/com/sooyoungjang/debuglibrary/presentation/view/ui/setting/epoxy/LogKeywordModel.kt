package com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy

import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingActivity

internal data class LogKeywordModel(
    val content: String,
    val callback: SettingActivity.Callback
)