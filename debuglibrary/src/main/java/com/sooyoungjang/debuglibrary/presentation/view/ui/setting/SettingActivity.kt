package com.sooyoungjang.debuglibrary.presentation.view.ui.setting

import SettingScreen
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.di.AppContainer
import com.sooyoungjang.debuglibrary.presentation.view.ui.base.MaterialBaseTheme
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


internal class SettingActivity : AppCompatActivity() {
    private val appContainer: AppContainer by lazy { AppContainer(this) }
    private val viewModel: SettingViewModel by lazy { SettingViewModel(appContainer.sharedPreferencesUtil, appContainer.resourceProvider) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.setting)

        setContent {
            MaterialBaseTheme(isDynamicColor = true) {
                SettingScreen(viewModel = viewModel)
            }
        }
        initObservers()
    }

    private fun initObservers() {
        setupBackPressed()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effect.distinctUntilChanged().collect { effect ->
                    when (effect) {
                        SettingContract.SideEffect.OnBackPressed -> {
                            EventBus.getDefault().post(effect)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setEvent(SettingContract.Event.OnBackPressed)
            }
        })
    }

}