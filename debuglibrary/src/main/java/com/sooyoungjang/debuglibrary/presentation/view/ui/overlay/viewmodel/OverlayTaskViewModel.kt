package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.presentation.base.BaseViewModel
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskContract
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class OverlayTaskViewModel(
    private val getLogcatUseCase: GetLogcatUseCase,
    private val clearLogUseCase: ClearLogUseCase,
    private val deleteLogUseCase: DeleteLogUseCase,
    private val sharedPreferencesUtil: SharedPreferencesUtil,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel<OverlayTaskContract.Event, OverlayTaskContract.State, OverlayTaskContract.SideEffect>() {

    private lateinit var job: Job

    override fun createIdleState(): OverlayTaskContract.State {
        return OverlayTaskContract.State.idle()
    }

    override fun handleEvent(event: OverlayTaskContract.Event) {
        when (event) {
            is OverlayTaskContract.Event.OnCollectLog -> requestLogcats(event.keyword)
            is OverlayTaskContract.Event.OnKeywordItemClick -> selectKeyword(event.position)
            is OverlayTaskContract.Event.OnBackPressedClickFromSetting -> backPressedEvent()
            is OverlayTaskContract.Event.OnSearchClick -> searchLog(event.keyword)
            is OverlayTaskContract.Event.OnPageUpClick -> searchLogPageUp(event.keyword, event.currentPosition)
            is OverlayTaskContract.Event.OnPageDownClick -> searchLogPageDown(event.keyword, event.currentPosition)
            is OverlayTaskContract.Event.OnZoomLog -> onZoomLog(event.isZoom)
            OverlayTaskContract.Event.OnOpenClick -> expandView()
            OverlayTaskContract.Event.OnCloseClick -> setState { OverlayTaskContract.State.idle() }
            OverlayTaskContract.Event.OnCloseLongClick -> stopService()
            OverlayTaskContract.Event.OnClearClick -> clearLog()
            OverlayTaskContract.Event.DeleteLog -> deleteLog()
            OverlayTaskContract.Event.OnNavigateToSetting -> navigateToSetting()
            OverlayTaskContract.Event.OnNavigateToSearchIng -> navigateToSearchIng()
        }
    }

    private fun requestLogcats(searchTag: String) {
        cancelJob()
        clearLog()

        job = viewModelScope.launch {
            val params = GetLogcatUseCase.Params(searchTag.ifEmpty { "normal" })
            getLogcatUseCase.invoke(params)
                .map { it.map { LogUiModel(it.content, it.logLevel) } }
                .collect {
                    setState { copy(logs = it) }
                }
        }
    }

    private fun selectKeyword(position: Int) {
        val keyword = sharedPreferencesUtil.getFilterKeywordList()[position]
        requestLogcats(keyword)

        setState {
            copy(
                filterKeywordTitle = keyword,
                keywordSelectedPosition = position,
                filterKeywordList = sharedPreferencesUtil.getFilterKeywordList()
            )
        }
    }

    private fun expandView() {
        val keywords = sharedPreferencesUtil.getFilterKeywordList()
        val isDarkBackgroundColor = sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND)
        val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else R.color.transparent_gray
        val zoomHeight = (resourceProvider.getScreenHeight() / 3.5).toInt()

        requestLogcats(keywords.first())

        setState {
            copy(
                expandView = true,
                setting = true,
                keywordTitle = true,
                filterKeyword = true,
                filterKeywordList = keywords,
                filterKeywordTitle = keywords.first(),
                searching = false,
                trash = true,
                zoom = true,
                zoomHeight = zoomHeight,
                move = true,
                close = true,
                log = true,
                backgroundColor = backgroundColor
            )
        }
    }

    private fun searchLog(keyword: String) {
        try {
            if (keyword.isBlank()) throw IllegalStateException("pls, input keyword")
            val uiModels = currentState.logs.withIndex().filter { it.value.content.contains(keyword, true) }
            val position = uiModels.firstOrNull()?.index ?: throw IllegalStateException("keyword is not found")

            setState { copy(scrollPosition = position, searching = true, searchKeyword = keyword) }
        } catch (e: Exception) {
            setEffect { OverlayTaskContract.SideEffect.Error.NotFoundLog(e.message.toString()) }
        }
    }

    private fun searchLogPageUp(keyword: String, currentPosition: Int) {
        try {

            if (keyword.isBlank()) throw IllegalStateException("pls, input keyword")
            val uiModels = currentState.logs.withIndex().filter { it.value.content.contains(keyword, true) }
            val position = uiModels.findLast {
                it.index < currentPosition }?.index ?: throw IllegalStateException("Not found or The end has been reached.")

            setState { copy(scrollPosition = position) }
        } catch (e: Exception) {
            setEffect { OverlayTaskContract.SideEffect.Error.NotFoundLog(e.message.toString()) }
        }
    }

    private fun searchLogPageDown(keyword: String, currentPosition: Int) {
        try {
            if (keyword.isBlank()) throw IllegalStateException("pls, input keyword")
            val uiModels = currentState.logs.withIndex().filter { it.value.content.contains(keyword, true) }
            val position = uiModels.find {
                it.index > currentPosition }?.index ?: throw IllegalStateException("Not found or The end has been reached.")

            setState { copy(scrollPosition = position) }
        } catch (e: Exception) {
            setEffect { OverlayTaskContract.SideEffect.Error.NotFoundLog(e.message.toString()) }
        }
    }

    private fun backPressedEvent() {
        val isDarkBackgroundColor = sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND)
        val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else R.color.transparent_gray
        val keywords = sharedPreferencesUtil.getFilterKeywordList()
        requestLogcats(keywords.first())

        setState { copy(filterKeywordList = keywords, filterKeywordTitle = keywords.first(), backgroundColor = backgroundColor) }
    }

    private fun navigateToSetting() {
        setEffect { OverlayTaskContract.SideEffect.NavigateToSetting }
    }

    private fun navigateToSearchIng() {
        setEffect { OverlayTaskContract.SideEffect.NavigateToSearchIng }
    }

    private fun onZoomLog(isZoom: Boolean) {

        val height = if (isZoom.not()) {
            (resourceProvider.getScreenHeight() / 1.5).toInt()
        } else {
            (resourceProvider.getScreenHeight() / 3.5).toInt()
        }

        setState { copy(zoom = isZoom, zoomHeight = height) }
    }

    private fun clearLog() {
        viewModelScope.launch {
            clearLogUseCase.run(Unit)
        }
    }

    private fun deleteLog() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteLogUseCase.run(Unit)
        }
    }

    private fun stopService() {
        setEffect { OverlayTaskContract.SideEffect.StopService }
    }

    private fun cancelJob() {
        if (this::job.isInitialized) {
            job.cancel()
        }
    }

}
