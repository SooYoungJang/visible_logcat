package com.sooyoungjang.debuglibrary.presentation.view.ui.permission

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sooyoungjang.debuglibrary.di.AppContainer
import com.sooyoungjang.debuglibrary.di.DiManager
import com.sooyoungjang.debuglibrary.presentation.view.ui.base.MaterialBaseTheme
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.view.PermissionScreen
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.viewmodel.PermissionViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


internal class PermissionActivity : AppCompatActivity() {

    private val appContainer: AppContainer by lazy { DiManager.getInstance(this).appContainer }
    private val viewModel: PermissionViewModel by lazy { PermissionViewModel(appContainer.writeDataStoreUseCase, appContainer.resourceProvider) }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setFinishOnTouchOutside(false)
        super.onCreate(savedInstanceState)

        setContent {
            MaterialBaseTheme(isDynamicColor = true) {
                PermissionScreen(viewModel)
            }
        }

        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.collect { effect ->
                    when(effect) {
                        PermissionContract.SideEffect.CheckPermission -> checkPermission()
                        PermissionContract.SideEffect.Cancel -> finishActivity()
                        PermissionContract.SideEffect.NeverSeeAgainCancel -> finishActivityWithNeverSeeAgain()
                    }
                }
            }
        }
    }


    private fun checkPermission() {
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")).run { childForResult.launch(this) }
    }

    private fun finishActivity() {
        EventBus.getDefault().post(PermissionEvent.Deny)
        finish()
    }

    private fun finishActivityWithNeverSeeAgain() {
        EventBus.getDefault().post(PermissionEvent.NeverDeny)
        finish()
    }

    private val childForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (!Settings.canDrawOverlays(this)) {
                EventBus.getDefault().post(PermissionEvent.Deny)
            } else {
                EventBus.getDefault().post(PermissionEvent.Allow)
                finish()
            }
        }

}