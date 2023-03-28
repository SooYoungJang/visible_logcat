package com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel

import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.presentation.base.BaseViewModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.model.LogKeywordModel
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil

internal class SettingViewModel(
    private val sharedPreferencesUtil: SharedPreferencesUtil,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<SettingContract.Event, SettingContract.State, SettingContract.SideEffect>(){

    override fun createIdleState(): SettingContract.State {
        return SettingContract.State.idle()
    }

    init {
        initData()
    }

    private fun initData() {
        val keywords = sharedPreferencesUtil.getFilterKeywordList().map { LogKeywordModel(content = it) }
        val isDarkBackground = sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND)
        val textSizeTitle = resourceProvider.getString(R.string.change_text_size)
        val backgroundColorTitle = resourceProvider.getString(R.string.change_background_color)
        val filterKeywordTitle = resourceProvider.getString(R.string.save_filter_keyword)
        val filterKeywordListTitle = resourceProvider.getString(R.string.save_filter_keyword)
        val textSizes = resourceProvider.getStringList(R.array.text_size_array)
        val curTextSizeTitle = textSizes[sharedPreferencesUtil.getTextSizePosition()]

        setState {
            copy(
                curTextSizeValue = curTextSizeTitle,
                filterKeywordModels = keywords,
                darkBackground = isDarkBackground,
                textSizeTitle = textSizeTitle,
                textSizeList = textSizes,
                backgroundTitle = backgroundColorTitle,
                filterKeywordTitle = filterKeywordTitle,
                filterKeywordListTitle = filterKeywordListTitle
            )
        }
    }


    override fun handleEvent(event: SettingContract.Event) {
        when (event) {
            is SettingContract.Event.OnDarkBackgroundClick -> {
                sharedPreferencesUtil.putBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND, event.isAllow)

                setState { copy(darkBackground = event.isAllow) }
            }
            is SettingContract.Event.OnItemListSelectedPosition -> {
                sharedPreferencesUtil.putInt(Constants.SharedPreferences.EDDY_LOG_TEXT_SIZE, event.position)

                val curTextSizeTItle = resourceProvider.getStringList(R.array.text_size_array)[event.position]
                setState { copy(curTextSizeValue = curTextSizeTItle) }
            }
            is SettingContract.Event.OnAddFilterKeyword -> {
                sharedPreferencesUtil.putFilterKeyword(keyword = event.keyword)
                val keywordModels = sharedPreferencesUtil.getFilterKeywordList().map { LogKeywordModel(content = it) }
                setState { copy(filterKeywordModels = keywordModels) }
            }
            is SettingContract.Event.OnDeleteFilterKeyword -> {
                sharedPreferencesUtil.deleteFilterKeyword(keyword = event.keyword)
                val keywordModels = sharedPreferencesUtil.getFilterKeywordList().map { LogKeywordModel(content = it) }
                setState { copy(filterKeywordModels = keywordModels) }
            }
            SettingContract.Event.OnBackPressed -> setEffect { SettingContract.SideEffect.OnBackPressed }
        }
    }

}