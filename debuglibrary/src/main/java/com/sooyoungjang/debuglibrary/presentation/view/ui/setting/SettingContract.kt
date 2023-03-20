package com.sooyoungjang.debuglibrary.presentation.view.ui.setting

import android.util.Log
import com.sooyoungjang.debuglibrary.presentation.base.UiEffect
import com.sooyoungjang.debuglibrary.presentation.base.UiEvent
import com.sooyoungjang.debuglibrary.presentation.base.UiState
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel

internal class SettingContract {

    sealed interface Event : UiEvent {
        data class OnDarkBackgroundClick(val isAllow: Boolean): Event
        data class OnItemListSelectedPosition(val position: Int): Event
        data class OnAddFilterKeyword(val keyword: String): Event
        object OnBackPressed: Event
    }

    data class State(
        val curTextSizeListPosition: Int,
        val filterKeywordModels: List<LogKeywordModel>,
        val darkBackground: Boolean
    ) : UiState {
        companion object Factory {
            fun idle(): State {
                return State(
                    curTextSizeListPosition = 0,
                    filterKeywordModels = emptyList(),
                    darkBackground = false
                )
            }
        }
    }

    sealed interface SideEffect : UiEffect {
        object OnBackPressed: SideEffect
    }
}