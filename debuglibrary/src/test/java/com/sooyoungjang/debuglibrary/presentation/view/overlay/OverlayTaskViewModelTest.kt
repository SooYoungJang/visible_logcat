package com.sooyoungjang.debuglibrary.presentation.view.overlay

import com.appmattus.kotlinfixture.kotlinFixture
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.domain.log.model.LogLevel
import com.sooyoungjang.debuglibrary.domain.log.model.LogModel
import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCaseTest
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel.OverlayTaskViewModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionContract
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
class OverlayTaskViewModelTest : BehaviorSpec({

    coroutineTestScope = true
    isolationMode = IsolationMode.InstancePerLeaf
    val testDispatcher = UnconfinedTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    val fixture = kotlinFixture()

    val getLogcatUseCase: GetLogcatUseCase = mockk(relaxed = true)
    val clearLogUseCase: ClearLogUseCase = mockk(relaxed = true)
    val deleteLogUseCase: DeleteLogUseCase = mockk(relaxed = true)
    val sharedPreferencesUtil: SharedPreferencesUtil = mockk(relaxed = true)
    val resourceProvider: ResourceProvider = mockk(relaxed = true)

    Given("viewModel 을 생성하고") {
        val viewModel = OverlayTaskViewModel(getLogcatUseCase, clearLogUseCase, deleteLogUseCase, sharedPreferencesUtil, resourceProvider)
        val idleState = viewModel.createIdleState()

        When("대기 상태 일 때") {
            Then("State 값은 Idle 상태 이다.") {
                assertEquals(OverlayTaskContract.State.idle(), idleState)
            }
        }

        When("open event 가 발생 할 때") {
            val event = OverlayTaskContract.Event.OnOpenClick
            val keywords = listOf("a", "b", "c")
            val isDarkBackgroundColor = fixture<Boolean>()
            val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else R.color.transparent_gray

            every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords
            every { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) } returns isDarkBackgroundColor

            val excepted = OverlayTaskContract.State.idle().copy(
                expandView = true,
                setting = true,
                keywordTitle = true,
                filterKeyword = true,
                filterKeywordList = keywords,
                filterKeywordTitle = keywords[0],
                searching = true,
                trash = true,
                zoom = true,
                move = true,
                close = true,
                log = true,
                backgroundColor = backgroundColor
            )

            viewModel.handleEvent(event)

            Then("State 값을 변경 한다.") {
                verify { sharedPreferencesUtil.getFilterKeywordList() }
                verify { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) }

                assertEquals(excepted, viewModel.currentState)
            }
        }

        When("close event 가 발생 할 때") {
            val event = OverlayTaskContract.Event.OnCloseClick
            val excepted = OverlayTaskContract.State.idle()

            viewModel.handleEvent(event)

            Then("State 를 대기 상태로 변경 한다.") {
                assertEquals(excepted, viewModel.currentState)
            }
        }

        When("물리적 backPress 를 클릭하면") {
            val keywords = listOf("a", "b", "c")
            val isDarkBackgroundColor = fixture<Boolean>()
            val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else R.color.transparent_gray
            val event = OverlayTaskContract.Event.OnBackPressedClickFromSetting


            every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords
            every { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) } returns isDarkBackgroundColor
            val excepted = OverlayTaskContract.SideEffect.BackPressed(filterKeywordList = sharedPreferencesUtil.getFilterKeywordList(), backgroundColor = backgroundColor)

            viewModel.handleEvent(event)

            then("filterKeywordList sideEffect 를 방출 한다.") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("filter keyword 를 선택 할 때") {
            val position = fixture<Int>()
            val keyword = fixture<String>()
            val event = OverlayTaskContract.Event.OnKeywordItemClick(position)
            val logModels = fixture<List<LogModel>>()
            val logUiModels = logModels.map { LogUiModel(content = it.content, contentLevel = it.logLevel) }

            val exceptedSideEffect = OverlayTaskContract.SideEffect.FetchLogs(logUiModels)
            val exceptedState = OverlayTaskContract.State.idle().copy(
                filterKeywordTitle = keyword,
                keywordSelectedPosition = position,
                filterKeywordList = sharedPreferencesUtil.getFilterKeywordList()
            )

            every { sharedPreferencesUtil.getFilterKeywordList()[position] } returns keyword
            coEvery { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) } returns flowOf(logModels)

            viewModel.handleEvent(event)

            Then("sideEffect 및 state 를 변경 한다 ") {
                verify { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) }

                assertEquals(exceptedSideEffect, viewModel.effect.first())
                assertEquals(exceptedState, viewModel.currentState)
            }
        }

        When("clear event 가 발샏 할 때") {
            val event = OverlayTaskContract.Event.OnClearClick
            viewModel.handleEvent(event)

            Then("clear usecase 를 실행 한다") {
                coVerify { clearLogUseCase.run(Unit) }
            }
        }

        When("delete event 가 발생 할 때") {
            val event = OverlayTaskContract.Event.DeleteLog
            viewModel.handleEvent(event)

            Then("delete Log UseCase 를 실행 한다.") {
                coVerify { deleteLogUseCase.run(Unit) }
            }
        }

        When("검색 완료 버튼을 눌렀을 때 models 에서 keyword 와 일치 한다면 ") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22"
            val event = OverlayTaskContract.Event.OnSearchClick(logUiModels, searchKeyword)

            val exceptedPosition =
                logUiModels.withIndex().find { it.value.content.contains(searchKeyword) }?.index ?: throw IllegalStateException("Not found or The end has been reached.")
            val excepted = OverlayTaskContract.SideEffect.SearchLog(searchKeyword, exceptedPosition)

            viewModel.handleEvent(event)

            Then("sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("검색 완료 버튼을 눌렀을 때, models 에서 keyword 를 찾지 못한 다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 44"
            val event = OverlayTaskContract.Event.OnSearchClick(logUiModels, searchKeyword)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("List is empty.")

            viewModel.handleEvent(event)

            Then("sideEffect 를 방출 한다 ") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("업 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 크다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22" //1
            val curPosition = 0
            val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

            viewModel.handleEvent(event)

            Then("sideEffect 를 방출 한다 ") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("업 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 작다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22" //index 1
            val curPosition = 2
            val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

            val exceptedPosition = 1
            val excepted = OverlayTaskContract.SideEffect.ScrollPosition(exceptedPosition)

            viewModel.handleEvent(event)

            Then("sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("업 버튼을 눌렀을 때, 현재 포지션과 찾으려는 키워드의 인덱스가 같다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22" //index 1
            val curPosition = 1
            val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

            viewModel.handleEvent(event)

            Then("error sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("업 버튼을 눌렀을 때, 키워드가 공백이라면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = ""
            val curPosition = 2
            val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("pls, input keyword")

            viewModel.handleEvent(event)

            Then("NotFound error sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("업 버튼을 눌렀을 때, 모델 데이터가 비어 있다면") {
            val logUiModels = emptyList<LogUiModel>()
            val searchKeyword = "test 11"
            val curPosition = 0
            val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

            viewModel.handleEvent(event)

            Then("NotFound error SideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("다운 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 크다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22"
            val curPosition = 0
            val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

            val exceptedPosition = 1
            val excepted = OverlayTaskContract.SideEffect.ScrollPosition(exceptedPosition)

            viewModel.handleEvent(event)

            Then("sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("다운 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 작다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22" //index 1
            val curPosition = 2
            val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

            viewModel.handleEvent(event)

            Then("error sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("다운 버튼을 눌렀을 때, 현재 포지션과 찾으려는 키워드의 인덱스가 같다면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = "test 22" //index 1
            val curPosition = 1
            val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

            viewModel.handleEvent(event)

            Then("error sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("다운 버튼을 눌렀을 때, 키워드가 공백이라면") {
            val logUiModels = listOf(
                LogUiModel(content = "test 11", contentLevel = LogLevel.D),
                LogUiModel(content = "test 22", contentLevel = LogLevel.I),
                LogUiModel(content = "test 33", contentLevel = LogLevel.W)
            )
            val searchKeyword = ""
            val curPosition = 2
            val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("pls, input keyword")

            viewModel.handleEvent(event)

            Then("NotFound error sideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("다운 버튼을 눌렀을 때, 모델 데이터가 비어 있다면") {
            val logUiModels = emptyList<LogUiModel>()
            val searchKeyword = "test 11"
            val curPosition = 0
            val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

            val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

            viewModel.handleEvent(event)

            Then("NotFound error SideEffect 를 방출 한다") {
                assertEquals(excepted, viewModel.effect.first())
            }
        }

        When("OnCollectLog event 실행 되면") {
            val keyword = fixture<String>()
            val event = OverlayTaskContract.Event.OnCollectLog(keyword)
            val logModels = fixture<List<LogModel>>()
            val logUiModels = logModels.map { LogUiModel(content = it.content, contentLevel = it.logLevel) }
            val excepted = OverlayTaskContract.SideEffect.FetchLogs(logUiModels)

            coEvery { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) } returns flowOf(logModels)

            viewModel.handleEvent(event)

            Then("sideEffect 를 방출 한다") {
                verify { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) }

                assertEquals(excepted, viewModel.effect.first())
            }
        }
    }

})