package com.eddy.debuglibrary.presentation.view.ui.setting.viewmodel

import com.appmattus.kotlinfixture.kotlinFixture
import com.eddy.debuglibrary.presentation.view.ui.setting.SettingContract
import com.eddy.debuglibrary.util.Constants
import com.eddy.debuglibrary.util.ResourceProvider
import com.eddy.debuglibrary.util.SharedPreferencesUtil
import com.eddy.debuglibrary.util.di.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineRule::class)
class SettingViewModelTest {
    private lateinit var viewModel: SettingViewModel
    val fixture = kotlinFixture()

    private val sharedPreferencesUtil: SharedPreferencesUtil = mockk(relaxed = true) {
        every { getTextSizePosition() } returns fixture()
        every { getFilterKeywordList() } returns fixture()
        every { getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) } returns fixture()
    }

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @BeforeEach
    fun setup() {
        viewModel = SettingViewModel(sharedPreferencesUtil)
    }

    @Test
    fun `viewModel 의 초기 State 값은 initial 이다 `() {
        val initialState = viewModel.createInitialState()
        assertEquals(SettingContract.State.initial(), initialState)
    }

    @Test
    fun `배경 진하게 버튼 을 활성화 하면 darkBackground 상태 를 event 에서 발행한 상태로 변경 한다 `() {
        val isDarkBackground = fixture<Boolean>()
        val event = SettingContract.Event.OnDarkBackgroundClick(isDarkBackground)

        viewModel.handleEvent(event)

        assertEquals(viewModel.currentState.darkBackground, event.isAllow)
    }

    @Test
    fun `필터 키워드를 삭제 하면 SideEffect가 발생 한다 `() = runTest {
        val keyword = "test keyword"

        viewModel.onClickDeleteKeyword(keyword)

        assertEquals(SettingContract.SideEffect.DeleteKeyword(keyword), viewModel.effect.first())
    }

    @Test
    fun `keyword 가 공백 이어도 delete를 하면 SideEffect가 발생 한다 `() = runTest {
        val keyword = ""

        viewModel.onClickDeleteKeyword(keyword)

        assertEquals(SettingContract.SideEffect.DeleteKeyword(keyword), viewModel.effect.first())
    }

//    @Test
//    fun `textSize를 선택하면 State 변경 된다 `() {
//        val textSize = 10
//
//        viewModel.onClickTextSize(textSize)
//
//        assertEquals(viewModel.currentState.curTextSizeListPosition, textSize)
//    }
}