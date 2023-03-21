package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import com.sooyoungjang.debuglibrary.presentation.base.UiEffect
import com.sooyoungjang.debuglibrary.presentation.base.UiEvent
import com.sooyoungjang.debuglibrary.presentation.base.UiState
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel

internal class OverlayTaskContract {

    sealed interface Event : UiEvent {
        object OnOpenClick: Event
        object OnCloseClick: Event
        object OnClearClick: Event
        data class OnKeyWordItemClick(val keyWord: String) : Event
        object DeleteLog: Event
    }

    data class State(
        val expandView: Boolean,
        val setting: Boolean,
        val keywordTitle: Boolean,
        val filterKeyword: Boolean,
        val filterKeywordList: List<String>,
        val searching: Boolean,
        val searchLayout: Boolean,
        val trash: Boolean,
        val zoom: Boolean,
        val zoomChecked: Boolean,
        val move: Boolean,
        val close: Boolean,
        val log: Boolean,
        val logTitle: String,
        val keywordSelectedPosition: Int
    ) : UiState {
        companion object Factory {
            fun idle(): State {
                return State(
                    setting = false,
                    keywordTitle = true,
                    filterKeyword = false,
                    filterKeywordList = listOf(),
                    searching = false,
                    searchLayout = false,
                    trash = false,
                    zoom = false,
                    move = true,
                    close = false,
                    expandView = false,
                    zoomChecked = false,
                    log = false,
                    logTitle = "log",
                    keywordSelectedPosition = 0
                )
            }
        }
    }


    sealed interface SideEffect : UiEffect {
        data class FetchLogs(val logs : List<LogUiModel>) : SideEffect
    }

}

