package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel

import android.R
import android.widget.ArrayAdapter
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.presentation.base.BaseViewModel
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel
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
            is OverlayTaskContract.Event.OnKeyWordItemClick -> requestLogcats(event.keyWord)
            is OverlayTaskContract.Event.OnClearClick -> clearLog()
            is OverlayTaskContract.Event.DeleteLog -> deleteLog()
        }
    }

    private fun expandView() {
        val keywords = sharedPreferencesUtil.getFilterKeywordList()

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
                log = true
            )
        }
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
