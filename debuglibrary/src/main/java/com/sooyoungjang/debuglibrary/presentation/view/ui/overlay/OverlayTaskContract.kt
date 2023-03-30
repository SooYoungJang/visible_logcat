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
        object OnCloseLongClick: Event
        object DeleteLog: Event
        data class OnZoomLog(val isZoom: Boolean): Event
        data class OnCollectLog(val keyword: String): Event
        data class OnKeywordItemClick(val position: Int) : Event
        object OnNavigateToSetting: Event
        object OnNavigateToSearchIng: Event
        object OnBackPressedClickFromSetting : Event
        data class OnSearchClick(val keyword: String): Event
        data class OnPageUpClick(val keyword:String, val currentPosition: Int): Event
        data class OnPageDownClick(val keyword: String, val currentPosition: Int): Event
    }

    data class State(
        val expandView: Boolean,
        val setting: Boolean,
        val keywordTitle: Boolean,
        val filterKeyword: Boolean,
        val filterKeywordList: List<String>,
        val filterKeywordTitle: String,
        val searching: Boolean,
        val searchKeyword: String,
        val searchLayout: Boolean,
        val trash: Boolean,
        val zoom: Boolean,
        val zoomHeight: Int,
        val move: Boolean,
        val close: Boolean,
        val log: Boolean,
        val logTitle: String,
        val keywordSelectedPosition: Int,
        val backgroundColor: Int,
        val logs : List<LogUiModel>,
        val scrollPosition: Int
    ) : UiState {
        companion object Factory {
            fun idle(): State {
                return State(
                    setting = false,
                    keywordTitle = true,
                    filterKeyword = false,
                    filterKeywordList = listOf(),
                    filterKeywordTitle = "",
                    searching = false,
                    searchLayout = false,
                    trash = false,
                    zoom = false,
                    move = true,
                    close = false,
                    expandView = false,
                    zoomHeight = 300,
                    log = false,
                    logTitle = "log",
                    keywordSelectedPosition = 0,
                    backgroundColor = 0,
                    logs = listOf(),
                    scrollPosition = 0,
                    searchKeyword = ""
                )
            }
        }
    }


    sealed interface SideEffect : UiEffect {
        object NavigateToSetting: SideEffect
        object NavigateToSearchIng: SideEffect
        object StopService: SideEffect

        sealed interface Error: SideEffect {
            data class NotFoundLog(val message: String): Error
        }
    }

}

