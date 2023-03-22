package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import android.view.WindowInsets.Side
import com.sooyoungjang.debuglibrary.presentation.base.UiEffect
import com.sooyoungjang.debuglibrary.presentation.base.UiEvent
import com.sooyoungjang.debuglibrary.presentation.base.UiState
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import kotlin.Error

internal class OverlayTaskContract {

    sealed interface Event : UiEvent {
        object OnOpenClick: Event
        object OnCloseClick: Event
        object OnClearClick: Event
        object DeleteLog: Event
        data class OnKeywordItemClick(val keyWord: String) : Event
        data class OnBackPressedClickFromSetting(val keyWord: String) : Event
        data class OnSearchClick(val searchKeyword: String ,val logUiModels: List<LogUiModel>?): Event
        data class OnPageUpClick(val logUiModels: List<LogUiModel>?, val currentPosition: Int): Event
        data class OnPageDownClick(val logUiModels: List<LogUiModel>?, val currentPosition: Int): Event
    }

    data class State(
        val expandView: Boolean,
        val setting: Boolean,
        val keywordTitle: Boolean,
        val filterKeyword: Boolean,
        val filterKeywordList: List<String>,
        val searching: Boolean,
        val searchKeyword: String,
        val searchLayout: Boolean,
        val trash: Boolean,
        val zoom: Boolean,
        val zoomChecked: Boolean,
        val move: Boolean,
        val close: Boolean,
        val log: Boolean,
        val logTitle: String,
        val keywordSelectedPosition: Int,
        val backgroundColor: Int,
    ) : UiState {
        companion object Factory {
            fun idle(): State {
                return State(
                    setting = false,
                    keywordTitle = true,
                    filterKeyword = false,
                    filterKeywordList = listOf(),
                    searching = false,
                    searchKeyword = "",
                    searchLayout = false,
                    trash = false,
                    zoom = false,
                    move = true,
                    close = false,
                    expandView = false,
                    zoomChecked = false,
                    log = false,
                    logTitle = "log",
                    keywordSelectedPosition = 0,
                    backgroundColor = 0,
                )
            }
        }
    }


    sealed interface SideEffect : UiEffect {
        data class FetchLogs(val logs : List<LogUiModel>): SideEffect
        data class SearchLog(val keyword: String, val position: Int): SideEffect
        data class ScrollPosition(val position: Int): SideEffect

        sealed interface Error: SideEffect {
            data class NotFoundLog(val message: String): Error
        }
    }

}

