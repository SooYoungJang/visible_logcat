package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel

import android.util.Log
import android.widget.Toast
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

    private fun requestLogcats(searchTag: String) {
        cancelJob()
        clearLog()

        job = viewModelScope.launch {
            val params = GetLogcatUseCase.Params(searchTag.ifEmpty { "normal" })
            getLogcatUseCase.invoke(params)
                .map { it.map { LogUiModel(it.content, it.logLevel) } }
                .collect {
                    setEffect { OverlayTaskContract.SideEffect.FetchLogs(it) }
                }
        }
    }

    override fun createIdleState(): OverlayTaskContract.State {
        return OverlayTaskContract.State.idle()
    }

    override fun handleEvent(event: OverlayTaskContract.Event) {
        when (event) {
            OverlayTaskContract.Event.OnOpenClick -> expandView()
            is OverlayTaskContract.Event.OnCloseClick -> setState { OverlayTaskContract.State.idle() }
            is OverlayTaskContract.Event.OnKeywordItemClick -> requestLogcats(event.keyWord)
            is OverlayTaskContract.Event.OnClearClick -> clearLog()
            is OverlayTaskContract.Event.DeleteLog -> deleteLog()
            is OverlayTaskContract.Event.OnBackPressedClickFromSetting -> backPressedEvent(event.keyWord)
            is OverlayTaskContract.Event.OnSearchClick -> searchLog(event.searchKeyword, event.logUiModels)
            is OverlayTaskContract.Event.OnPageUpClick -> searchLogPageUp(event.logUiModels, event.currentPosition)
            is OverlayTaskContract.Event.OnPageDownClick -> searchLogPageDown(event.logUiModels, event.currentPosition)
        }
    }

    private fun expandView() {
        val keywords = sharedPreferencesUtil.getFilterKeywordList()
        val isDarkBackgroundColor = sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND)
        val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else R.color.transparent_gray

        setState {
            copy(
                expandView = true,
                setting = true,
                keywordTitle = true,
                filterKeyword = true,
                filterKeywordList = keywords,
                searching = true,
                trash = true,
                zoom = true,
                zoomChecked = false,
                move = true,
                close = true,
                log = true,
                backgroundColor = backgroundColor
            )
        }
    }

    private fun searchLog(searchKeyword: String,  logUiModes: List<LogUiModel>?) {
        try {
            val uiModels = logUiModes?.withIndex()?.filter { it.value.content.contains(searchKeyword, true) }
            val position = uiModels?.first()?.index ?: throw IllegalStateException("Not found.")

            setState { copy(searchKeyword = searchKeyword) }
            setEffect { OverlayTaskContract.SideEffect.SearchLog(searchKeyword, position = position) }
        } catch (e: Exception) {
            setEffect { OverlayTaskContract.SideEffect.Error.NotFoundLog(e.message.toString()) }
        }

    }

    private fun searchLogPageUp(logUiModes: List<LogUiModel>?, currentPosition: Int) {
        try {
            val uiModels = logUiModes?.withIndex()?.filter { it.value.content.contains(currentState.searchKeyword) }
            val position = uiModels?.findLast { it.index < currentPosition }?.index ?: throw IllegalStateException("Not found or The end has been reached.")

            setEffect { OverlayTaskContract.SideEffect.ScrollPosition(position) }
        } catch (e: Exception) {
            setEffect { OverlayTaskContract.SideEffect.Error.NotFoundLog(e.message.toString()) }
        }
    }

    private fun searchLogPageDown(logUiModes: List<LogUiModel>?, currentPosition: Int) {
        try {
            val uiModels = logUiModes?.withIndex()?.filter { it.value.content.contains(currentState.searchKeyword) }
            val position = uiModels?.find { it.index > currentPosition }?.index ?: throw IllegalStateException("Not found or The end has been reached.")

            setEffect { OverlayTaskContract.SideEffect.ScrollPosition(position) }
        } catch (e: Exception) {
            setEffect { OverlayTaskContract.SideEffect.Error.NotFoundLog(e.message.toString()) }
        }
    }

    private fun backPressedEvent(keyword: String) {
        requestLogcats(keyword)
        setState { copy(filterKeywordList = sharedPreferencesUtil.getFilterKeywordList()) }
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

    private fun cancelJob() {
        if (this::job.isInitialized) {
            job.cancel()
        }
    }

}
