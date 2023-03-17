package com.eddy.debuglibrary.presentation.viewmodel

import com.eddy.debuglibrary.presentation.base.UiEffect
import com.eddy.debuglibrary.presentation.base.UiEvent
import com.eddy.debuglibrary.presentation.base.UiState
import com.eddy.debuglibrary.presentation.view.model.LogForm
import com.eddy.debuglibrary.presentation.view.model.LogUiModel

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

