package com.sooyoungjang.debuglibrary.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.presentation.base.BaseViewModel
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class OverlayTaskViewModel(
    private val getLogcatUseCase: GetLogcatUseCase,
    private val clearLogUseCase: ClearLogUseCase,
    private val deleteLogUseCase: DeleteLogUseCase,
    private val resourceProvider: ResourceProvider,
    ) : BaseViewModel<OverlayContract.Event, OverlayContract.State, OverlayContract.SideEffect>() {

    private lateinit var job: Job

    fun requestLogcats(searchTag: String) {
        cancelJob()
        clearLog()

        job = viewModelScope.launch {
            val params = GetLogcatUseCase.Params(searchTag.ifEmpty { "normal" })
            getLogcatUseCase.invoke(params)
                .map { it.map { LogUiModel(it.content, it.logLevel) } }
                .collect {
                    setEffect { OverlayContract.SideEffect.FetchLogs(it) }
                }
        }
    }

    private fun cancelJob() {
        if (this::job.isInitialized) {
            job.cancel()
        }
    }

    override fun createInitialState(): OverlayContract.State {
        return OverlayContract.State(OverlayContract.LogsState.Idle)
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

    override fun handleEvent(event: OverlayContract.Event) {
        when (event) {
            is OverlayContract.Event.OnCloseClick -> setState { copy(logsState = OverlayContract.LogsState.Idle) }

            is OverlayContract.Event.OnClickKeyWordItem -> requestLogcats(event.keyWord)

            is OverlayContract.Event.OnClearClick -> clearLog()

            is OverlayContract.Event.DeleteLog -> deleteLog()

        }
    }

}
