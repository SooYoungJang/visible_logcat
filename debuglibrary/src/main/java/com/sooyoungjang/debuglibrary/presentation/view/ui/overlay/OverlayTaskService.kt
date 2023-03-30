package com.sooyoungjang.debuglibrary.presentation.view.ui.overlay

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.*
import android.widget.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.*
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.sooyoungjang.debuglibrary.di.AppContainer
import com.sooyoungjang.debuglibrary.di.DiManager
import com.sooyoungjang.debuglibrary.presentation.view.ui.base.MaterialBaseTheme
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.view.OverlayTaskScreen
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel.OverlayTaskViewModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.search.SearchActivity
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingActivity
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@SuppressLint("ClickableViewAccessibility")
internal class OverlayTaskService : LifecycleService() {
    private val windowManager: WindowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }

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

    private lateinit var unBindCallback: () -> Unit

    private val params: WindowManager.LayoutParams by lazy {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT
        )
    }

    private val composeView: ComposeView by lazy { ComposeView(this) }

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        showOverlay()
        onClickDelete()
        initObservers()
    }

    private fun showOverlay() {

        composeView.setContent {
            MaterialBaseTheme(true) {
                OverlayTaskScreen(viewModel, ge)
            }
        }

        val viewModelStore = ViewModelStore()
        val lifecycleOwner = OverlayTaskLifecycleOwner()
        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_START)

        val viewModelStoreOwner = object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore
                get() = viewModelStore
        }

        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        windowManager.addView(composeView, params)
    }

    private val ge: (Float, Float) -> Unit = { x, y ->
        params.x = x.toInt()
        params.y = y.toInt()
        windowManager.updateViewLayout(composeView, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val searchKeyword = intent?.getStringExtra(SEARCH_KEYWORD) ?: ""
        onClickSearch(searchKeyword)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        windowManager.removeView(composeView)
        return super.onUnbind(intent)
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect {
                    when (it) {
                        is OverlayTaskContract.SideEffect.Error.NotFoundLog -> makeToast(it.message)
                        OverlayTaskContract.SideEffect.NavigateToSetting -> navigateToSetting()
                        OverlayTaskContract.SideEffect.NavigateToSearchIng -> navigateToSearching()
                        OverlayTaskContract.SideEffect.StopService -> stopService()
                    }
                }
            }
        }
    }

    internal fun setUnBindServiceCallback(block: () -> Unit) {
        this.unBindCallback = block
    }

    private fun stopService() {
        unBindCallback.invoke()
    }

    private fun onClickDelete() {
        viewModel.setEvent(OverlayTaskContract.Event.DeleteLog)
    }

    private fun onClickBackPressed() {
        viewModel.setEvent(OverlayTaskContract.Event.OnBackPressedClickFromSetting)
    }

    private fun onClickSearch(keyword: String) {
        viewModel.setEvent(OverlayTaskContract.Event.OnSearchClick(keyword))
    }

    private fun navigateToSetting() {
        windowManager.removeView(composeView)

        val intent = Intent(this, SettingActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun navigateToSearching() {
        val intent = Intent(this, SearchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


    @Subscribe
    fun settingEventHandler(event: SettingContract.SideEffect) {
        when (event) {
            SettingContract.SideEffect.OnBackPressed -> {
                windowManager.addView(composeView, params)
                onClickBackPressed()
            }
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        stopSelf()
    }

    companion object {
        const val SEARCH_KEYWORD = "Search Keyword"
    }
}