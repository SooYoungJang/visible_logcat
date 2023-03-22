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
import kotlin.math.log

@SuppressLint("ClickableViewAccessibility")
internal class OverlayTaskService : LifecycleService(), OverlayTaskCallback {

    inner class OverlayDebugToolPopUpBinder : Binder() {
        fun getService(): OverlayTaskService {
            return this@OverlayTaskService
        }
    }

    private val appContainer: AppContainer by lazy { DiManager.getInstance(this).appContainer }
    private val viewModel: OverlayTaskViewModel by lazy { OverlayTaskViewModel(appContainer.getLogcatUseCase, appContainer.clearLogUseCase, appContainer.deleteLogUseCase, appContainer.sharedPreferencesUtil, appContainer.resourceProvider) }
    private val sharedPreferences: SharedPreferences by lazy {getSharedPreferences(Constants.SharedPreferences.EDDY_DEBUG_TOOL, Context.MODE_PRIVATE)}

    private val binder = OverlayDebugToolPopUpBinder()
    private val view: OverlayTaskView by lazy { OverlayTaskView(context = applicationContext, callback = this) }

    private lateinit var unBindCallback: () -> Unit

    override fun onCreate() {
        super.onCreate()
        initObservers()
        saveFilterKeywordList()
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
                    when (it) {
                        is OverlayTaskContract.SideEffect.FetchLogs -> view.fetchLogs(it.logs)
                        is OverlayTaskContract.SideEffect.ScrollPosition -> view.scrollPosition(it.position)
                        is OverlayTaskContract.SideEffect.SearchLog -> view.searchLog(it.keyword, it.position)
                        is OverlayTaskContract.SideEffect.Error.NotFoundLog -> view.makeToast(it.message)
                    }
                }
            }
        }
    }

    private fun saveFilterKeywordList() {
        if(sharedPreferences.getString(Constants.SharedPreferences.EDDY_LOG_FILTER_KEYWORD, null) == null) {
            var arrayListPrefs = ArrayList<String>()
            var stringPrefs : String? = null

            arrayListPrefs.add(0, "normal")
            stringPrefs = GsonBuilder().create().toJson(
                arrayListPrefs,
                object : TypeToken<ArrayList<String>>() {}.type
            )
            sharedPreferences.edit().apply {
                putString(Constants.SharedPreferences.EDDY_LOG_FILTER_KEYWORD, stringPrefs)
                apply()
            }
        }
    }

    internal fun setUnBindServiceCallback(block: () -> Unit) {
        this.unBindCallback = block
    }

    private fun onClickClose() {
        viewModel.setEvent(OverlayTaskContract.Event.OnCloseClick)
    }

    private fun onClickTagItem(tag: String) {
        viewModel.setEvent(OverlayTaskContract.Event.OnKeywordItemClick(tag))
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

    private fun onClickBackPressed(tag: String) {
        viewModel.setEvent(OverlayTaskContract.Event.OnBackPressedClickFromSetting(tag))
    }

    private fun onClickSearch(searchKeyword: String ,logUiModels: List<LogUiModel>?) {
        viewModel.setEvent(OverlayTaskContract.Event.OnSearchClick(searchKeyword, logUiModels))
    }

    private fun onClickPageUp(logUiModels: List<LogUiModel>?, currentPosition: Int) {
        viewModel.setEvent(OverlayTaskContract.Event.OnPageUpClick(logUiModels, currentPosition))
    }

    private fun onClickPageDown(logUiModels: List<LogUiModel>?, currentPosition: Int) {
        viewModel.setEvent(OverlayTaskContract.Event.OnPageDownClick(logUiModels, currentPosition))
    }

    override fun onDestroy() {
        super.onDestroy()
        view.onDestroyView()
        stopSelf()
    }

    override val onClickOpen: () -> Unit = ::onClickTitle
    override val onClickClose: () -> Unit = ::onClickClose
    override val onClickTagItem: (tag: String) -> Unit = ::onClickTagItem
    override val onClickBackPressed: (tag: String) -> Unit = :: onClickBackPressed
    override val onLongClickCloseService: () -> Unit = ::onLongClickCloseService
    override val onClickDelete: () -> Unit = ::onClickDelete
    override val onClickSearch: (searchKeyword: String ,logUiModels: List<LogUiModel>?) -> Unit = ::onClickSearch
    override val onClickPageUp: (logUiModels: List<LogUiModel>?, currentPosition: Int) -> Unit = ::onClickPageUp
    override val onClickPageDown: (logUiModels: List<LogUiModel>?, currentPosition: Int) -> Unit = ::onClickPageDown

    companion object {
        const val SEARCH_KEYWORD = "Search Keyword"
    }
}