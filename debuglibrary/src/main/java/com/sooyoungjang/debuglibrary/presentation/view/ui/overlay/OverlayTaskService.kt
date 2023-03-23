package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.view.*
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sooyoungjang.debuglibrary.di.AppContainer
import com.sooyoungjang.debuglibrary.di.DiManager
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel.OverlayTaskViewModel
import com.sooyoungjang.debuglibrary.util.Constants
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged

@SuppressLint("ClickableViewAccessibility")
internal class OverlayTaskService : LifecycleService(), OverlayTaskCallback {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val appContainer: AppContainer by lazy { DiManager.getInstance(this).appContainer }
    private val viewModel: OverlayTaskViewModel by lazy {
        OverlayTaskViewModel(
            appContainer.getLogcatUseCase,
            appContainer.clearLogUseCase,
            appContainer.deleteLogUseCase,
            appContainer.sharedPreferencesUtil,
            appContainer.resourceProvider
        )
    }

    private val binder = OverlayDebugToolPopUpBinder()
    private val view: OverlayTaskView by lazy { OverlayTaskView(context = applicationContext, callback = this) }

    private lateinit var unBindCallback: () -> Unit

    override fun onCreate() {
        super.onCreate()
        initObservers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val searchKeyword = intent?.getStringExtra(SEARCH_KEYWORD) ?: ""

        view.onSearchKeyword(searchKeyword)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        view.onDestroyView()
        return super.onUnbind(intent)
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    view.setState(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.distinctUntilChanged().collect {
                    view.setEffect(it)
                }
            }
        }
    }

    internal fun setUnBindServiceCallback(block: () -> Unit) {
        this.unBindCallback = block
    }

    private fun onClickClose() {
        viewModel.setEvent(OverlayTaskContract.Event.OnCloseClick)
    }

    private fun onClickTagItem(position: Int) {
        viewModel.setEvent(OverlayTaskContract.Event.OnKeywordItemClick(position))
    }

    private fun onLongClickCloseService() {
        unBindCallback.invoke()
    }

    private fun onClickDelete() {
        viewModel.setEvent(OverlayTaskContract.Event.DeleteLog)
    }

    private fun onClickTitle() {
        viewModel.setEvent(OverlayTaskContract.Event.OnOpenClick)
    }

    private fun onClickBackPressed() {
        viewModel.setEvent(OverlayTaskContract.Event.OnBackPressedClickFromSetting)
    }

    private fun onClickSearch(logUiModels: List<LogUiModel>?,keyword: String) {
        viewModel.setEvent(OverlayTaskContract.Event.OnSearchClick(logUiModels, keyword))
    }

    private fun onClickPageUp(logUiModels: List<LogUiModel>?,keyword: String, currentPosition: Int) {
        viewModel.setEvent(OverlayTaskContract.Event.OnPageUpClick(logUiModels, keyword, currentPosition))
    }

    private fun onClickPageDown(logUiModels: List<LogUiModel>?,keyword: String, currentPosition: Int) {
        viewModel.setEvent(OverlayTaskContract.Event.OnPageDownClick(logUiModels,keyword, currentPosition))
    }

    private fun onCollectLog(keyword: String) {
        viewModel.setEvent(OverlayTaskContract.Event.OnCollectLog(keyword))
    }

    override fun onDestroy() {
        super.onDestroy()
        view.onDestroyView()
        stopSelf()
    }

    override val onClickOpen: () -> Unit = ::onClickTitle
    override val onClickClose: () -> Unit = ::onClickClose
    override val onClickTagItem: (position: Int) -> Unit = ::onClickTagItem
    override val onCollectLog: (keyword: String) -> Unit = ::onCollectLog
    override val onClickBackPressed: () -> Unit = ::onClickBackPressed
    override val onLongClickCloseService: () -> Unit = ::onLongClickCloseService
    override val onClickDelete: () -> Unit = ::onClickDelete
    override val onClickSearch: (logUiModels: List<LogUiModel>?, keyword: String) -> Unit = ::onClickSearch
    override val onClickPageUp: (logUiModels: List<LogUiModel>?, keyword: String, currentPosition: Int) -> Unit = ::onClickPageUp
    override val onClickPageDown: (logUiModels: List<LogUiModel>?, keyword: String, currentPosition: Int) -> Unit = ::onClickPageDown

    companion object {
        const val SEARCH_KEYWORD = "Search Keyword"
    }
}