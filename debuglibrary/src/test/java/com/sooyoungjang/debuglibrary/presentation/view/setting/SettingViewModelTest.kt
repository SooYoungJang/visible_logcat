package com.sooyoungjang.debuglibrary.presentation.view.setting

import com.appmattus.kotlinfixture.kotlinFixture
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineRule::class)
class SettingViewModelTest {
    private lateinit var viewModel: SettingViewModel
    val fixture = kotlinFixture()

    private val sharedPreferencesUtil: SharedPreferencesUtil = mockk(relaxed = true) {
        every { getTextSizePosition() } returns fixture()
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

    //todo. 잘못 짜여진 테스트인데.. 실제 값을 어떻게 구해오지?..
    @Test
    fun ` 필터 키워드를 사용자가 추가하면 상태를 변경한다 `() {
        val keyword = fixture<String>()
        val event = SettingContract.Event.OnAddFilterKeyword(keyword)
        val keywords = fixture<List<String>>().toMutableList().also { it.add(0, event.keyword) }

        every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords
        viewModel.handleEvent(event)

        verify { sharedPreferencesUtil.putFilterKeyword(event.keyword) }
        verify { sharedPreferencesUtil.getFilterKeywordList() }

        val expectedModels = keywords.map { LogKeywordModel(content = it, callback = viewModel) }
        val actualModels = viewModel.currentState.filterKeywordModels
        assertEquals(expectedModels, actualModels)
    }

    @Test
    fun `필터 키워드를 삭제 하면 새로운 상태를 발생 한다 `() = runTest {
        val keyword = "test keyword"
        val keywords = fixture<List<String>>().toMutableList().also { it.add(0, keyword) }
        val removeKeywords = keywords.also { it.remove(keyword) }
        every { sharedPreferencesUtil.getFilterKeywordList() } returns removeKeywords

        viewModel.onClickDeleteKeyword(keyword)

        verify { sharedPreferencesUtil.deleteFilterKeyword(keyword) }
        verify { sharedPreferencesUtil.getFilterKeywordList() }

        val expectedModels = removeKeywords.map { LogKeywordModel(content = it, callback = viewModel) }
        val actualModels = viewModel.currentState.filterKeywordModels
        assertEquals(expectedModels, actualModels)

    }

    @Test
    fun `keyword 가 공백 이어도 삭제 하면 SideEffect가 발생 한다 `() = runTest {
        val keyword = ""

        val keywords = fixture<List<String>>().toMutableList().also { it.add(0, keyword) }
        val removeKeywords = keywords.also { it.remove(keyword) }
        every { sharedPreferencesUtil.getFilterKeywordList() } returns removeKeywords

        viewModel.onClickDeleteKeyword(keyword)

        verify { sharedPreferencesUtil.deleteFilterKeyword(keyword) }
        verify { sharedPreferencesUtil.getFilterKeywordList() }

        val expectedModels = removeKeywords.map { LogKeywordModel(content = it, callback = viewModel) }
        val actualModels = viewModel.currentState.filterKeywordModels
        assertEquals(expectedModels, actualModels)
    }

    @Test
    fun ` 텍스트 사이즈를 변경하면 선택한 텍스트의 position 사이즈로 textSizeListPosition을 변경 한다 `() {
        val textSizePosition = fixture(range = listOf(0,1,2,3,4,5,6,7,8))
        val event = SettingContract.Event.OnItemListSelectedPosition(textSizePosition)

        viewModel.handleEvent(event)

        assertEquals(viewModel.currentState.curTextSizeListPosition, event.position)
    }

    @Test
    fun `물리적 back button 을 누르면  해당하는 SideEffect 를 만든다 `() = runTest {
        viewModel.handleEvent(SettingContract.Event.OnBackPressed)

        assertEquals(viewModel.effect.first(), SettingContract.SideEffect.OnBackPressed)
    }

}