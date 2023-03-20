package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import com.sooyoungjang.debuglibrary.presentation.base.UiEffect
import com.sooyoungjang.debuglibrary.presentation.base.UiEvent
import com.sooyoungjang.debuglibrary.presentation.base.UiState
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel

internal class OverlayContract {

    sealed interface Event : UiEvent {
        object OnCloseClick: Event
        object OnClearClick: Event
        data class OnClickKeyWordItem(val keyWord: String) : Event
        object DeleteLog: Event
    }

    data class State(
        val logsState: LogsState
    ) : UiState

    sealed interface LogsState {
        object Idle : LogsState
    }


    sealed interface SideEffect : UiEffect {
        data class FetchLogs(val logs : List<LogUiModel>) : SideEffect
    }

}

