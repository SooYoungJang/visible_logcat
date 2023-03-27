package com.sooyoungjang.debuglibrary.presentation.view.setting

import com.appmattus.kotlinfixture.kotlinFixture
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.SettingContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.epoxy.LogKeywordModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given

@ExperimentalCoroutinesApi
class SettingViewModelTest : BehaviorSpec({
    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf

    val fixture = kotlinFixture()
    val testDispatcher = UnconfinedTestDispatcher()
    val sharedPreferencesUtil: SharedPreferencesUtil = mockk(relaxed = true)

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    Given("viewModel 을 생성하고, ") {
        val viewModel = SettingViewModel(sharedPreferencesUtil)
        val idleState = viewModel.createIdleState()

        When("대기 상태 일 때,") {
            Then("State 값은 Idle 이다") {
                assertEquals(SettingContract.State.idle(), idleState)
            }
        }

        When("init 이 완료 되면") {
            val expectedState = SettingContract.State.idle().copy(
                curTextSizeListPosition = sharedPreferencesUtil.getTextSizePosition(),
                filterKeywordModels = sharedPreferencesUtil.getFilterKeywordList().map { LogKeywordModel(content = it, callback = viewModel) },
                darkBackground = sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND)
            )
            Then("데이터의 상태는 저장 되어 있던 값으로 변경 한다.") {
                verify { sharedPreferencesUtil.getTextSizePosition() }
                verify { sharedPreferencesUtil.getFilterKeywordList() }
                verify { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) }

                assertEquals(expectedState, viewModel.currentState)
            }
        }

        When("배경 진하게 버튼을 클릭하고 event 로 true 를  전달 하면") {
            val isDarkBackground = fixture<Boolean>()
            val event = SettingContract.Event.OnDarkBackgroundClick(isDarkBackground)
            viewModel.handleEvent(event)

            Then("상태값도 true 가 된다.") {
                assertEquals(viewModel.currentState.darkBackground, event.isAllow)
            }
        }

        When("필터 키워드를 추가 하면") {
            val keyword = fixture<String>()
            val event = SettingContract.Event.OnAddFilterKeyword(keyword)
            val keywords = fixture<List<String>>().toMutableList().also { it.add(0, event.keyword) }
            every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords

            viewModel.handleEvent(event)

            Then("상태를 변경 한다.") {
                verify { sharedPreferencesUtil.putFilterKeyword(event.keyword) }
                verify { sharedPreferencesUtil.getFilterKeywordList() }

                val expectedModels = keywords.map { LogKeywordModel(content = it, callback = viewModel) }
                val actualModels = viewModel.currentState.filterKeywordModels
                assertEquals(expectedModels, actualModels)
            }
        }

        When("필터 키워드를 삭제 하면") {
            val keyword = "test keyword"
            val keywords = fixture<List<String>>().toMutableList().also { it.add(0, keyword) }
            val removeKeywords = keywords.also { it.remove(keyword) }
            every { sharedPreferencesUtil.getFilterKeywordList() } returns removeKeywords

            viewModel.onClickDeleteKeyword(keyword)

            Then("상태를 변경 한다.") {
                verify { sharedPreferencesUtil.deleteFilterKeyword(keyword) }
                verify { sharedPreferencesUtil.getFilterKeywordList() }

                val expectedModels = removeKeywords.map { LogKeywordModel(content = it, callback = viewModel) }
                val actualModels = viewModel.currentState.filterKeywordModels
                assertEquals(expectedModels, actualModels)
            }
        }

        When("keyword 가 공백 이고, 삭제할 때,") {
            val keyword = ""

            val keywords = fixture<List<String>>().toMutableList().also { it.add(0, keyword) }
            val removeKeywords = keywords.also { it.remove(keyword) }
            every { sharedPreferencesUtil.getFilterKeywordList() } returns removeKeywords

            viewModel.onClickDeleteKeyword(keyword)

            Then("sideEffect 를 방출 한다.") {
                verify { sharedPreferencesUtil.deleteFilterKeyword(keyword) }
                verify { sharedPreferencesUtil.getFilterKeywordList() }

                val expectedModels = removeKeywords.map { LogKeywordModel(content = it, callback = viewModel) }
                val actualModels = viewModel.currentState.filterKeywordModels
                assertEquals(expectedModels, actualModels)
            }
        }

        When("텍스트 사이즈를 변경할 때") {
            val textSizePosition = fixture(range = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8))
            val event = SettingContract.Event.OnItemListSelectedPosition(textSizePosition)
            viewModel.handleEvent(event)

            Then("텍스트의 position 사이즈로 textSizeListPosition을 변경 한다") {
                assertEquals(viewModel.currentState.curTextSizeListPosition, event.position)
            }
        }

        When("물리적 back button 을 클릭할 때") {
            viewModel.handleEvent(SettingContract.Event.OnBackPressed)
            Then("SideEffect 를 방출 한다.") {
                assertEquals(viewModel.effect.first(), SettingContract.SideEffect.OnBackPressed)
            }
        }

    }

})