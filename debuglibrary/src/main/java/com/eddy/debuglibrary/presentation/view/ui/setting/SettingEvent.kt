package com.eddy.debuglibrary.presentation.view.ui.setting

internal sealed interface SettingEvent {
    object OnBackPress: SettingEvent
}