package com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel

import android.util.Log
import com.sooyoungjang.debuglibrary.presentation.base.BaseViewModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingActivity
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil

internal class SettingViewModel(
    private val sharedPreferencesUtil: SharedPreferencesUtil
) : BaseViewModel<SettingContract.Event, SettingContract.State, SettingContract.SideEffect>(), SettingActivity.Callback {

    override fun createInitialState(): SettingContract.State {
        return SettingContract.State.initial()
    }

    init {
        val textSizePosition = sharedPreferencesUtil.getTextSizePosition()
        val keywords = sharedPreferencesUtil.getFilterKeywordList().map { LogKeywordModel(content = it, callback = this) }
        val isDarkBackground = sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND)

        Log.d("test","aaaaaaa lk $textSizePosition")
        setState { copy(curTextSizeListPosition = textSizePosition, filterKeywords = keywords ,darkBackground =  isDarkBackground) }
    }

    override fun handleEvent(event: SettingContract.Event) {
        when(event) {
            is SettingContract.Event.OnDarkBackgroundClick -> {
                sharedPreferencesUtil.putBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND, event.isAllow)

                setState { copy(darkBackground = event.isAllow) }
            }
            is SettingContract.Event.OnItemListSelectedPosition -> {
                sharedPreferencesUtil.putInt(Constants.SharedPreferences.EDDY_LOG_TEXT_SIZE, event.position)
                Log.d("Test","aaaaaaa ${event.position}")
                setState { copy(curTextSizeListPosition = event.position) }
            }
        }
    }

    private fun onClickDeleteKeyword(keyword: String) {
        setEffect { SettingContract.SideEffect.DeleteKeyword(keyword) }
    }

    override val onClickDeleteKeyword: (keyword: String) -> Unit get() = ::onClickDeleteKeyword

}