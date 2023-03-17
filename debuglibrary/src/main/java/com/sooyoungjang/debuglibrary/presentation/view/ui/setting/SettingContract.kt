package com.sooyoungjang.debuglibrary.presentation.view.ui.setting

import com.sooyoungjang.debuglibrary.presentation.base.UiEffect
import com.sooyoungjang.debuglibrary.presentation.base.UiEvent
import com.sooyoungjang.debuglibrary.presentation.base.UiState
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel

internal class SettingContract {

    sealed interface Event : UiEvent {
        data class OnDarkBackgroundClick(val isAllow: Boolean): Event
        data class OnItemListSelectedPosition(val position: Int): Event
    }

    data class State(
        val curTextSizeListPosition: Int,
        val filterKeywords: List<LogKeywordModel>,
        val darkBackground: Boolean
    ) : UiState {
        companion object Factory {
            fun initial(): State {
                return State(
                    curTextSizeListPosition = 0,
                    filterKeywords = emptyList(),
                    darkBackground = false
                )
            }
        }
    }

    sealed interface SideEffect : UiEffect {
        data class DeleteKeyword(val keyword: String) : SideEffect
    }
}