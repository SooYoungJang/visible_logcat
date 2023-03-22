package com.sooyoungjang.debuglibrary.presentation.view.overlay

import com.appmattus.kotlinfixture.kotlinFixture
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.domain.log.model.LogLevel
import com.sooyoungjang.debuglibrary.domain.log.model.LogModel
import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.presentation.view.model.LogUiModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel.OverlayTaskViewModel
import com.sooyoungjang.debuglibrary.util.Constants
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.math.log

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineRule::class)
class OverlayTaskViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: OverlayTaskViewModel
    private val fixture = kotlinFixture()

    private val getLogcatUseCase: GetLogcatUseCase = mockk(relaxed = true)
    private val clearLogUseCase: ClearLogUseCase = mockk(relaxed = true)
    private val deleteLogUseCase: DeleteLogUseCase = mockk(relaxed = true)
    private val sharedPreferencesUtil: SharedPreferencesUtil = mockk(relaxed = true)
    private val resourceProvider: ResourceProvider = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        viewModel = OverlayTaskViewModel(getLogcatUseCase, clearLogUseCase, deleteLogUseCase, sharedPreferencesUtil, resourceProvider)
    }

    @Test
    fun `viewModel 의 초기 State 값은 idle 이다 `() {
        val idleState = viewModel.createIdleState()

        assertEquals(OverlayTaskContract.State.idle(), idleState)
    }

    @Test
    fun `open event 가 요청되면 새로운 상태를 방출 한다 `() {
        //given
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

        //when
        viewModel.handleEvent(event)


        //then
        verify { sharedPreferencesUtil.getFilterKeywordList() }
        verify { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) }

        assertEquals(excepted, viewModel.currentState)
    }

    @Test
    fun `close event 가 요청되면 초기 상태를 방출 한다 `() {
        //given
        val event = OverlayTaskContract.Event.OnCloseClick
        val excepted = OverlayTaskContract.State.idle()

        //when
        viewModel.handleEvent(event)

        //then
        assertEquals(excepted, viewModel.currentState)
    }

    @Test
    fun `setting activity 에서 backPress 를 누르면 filterKeywordList sideEffect 를 방출 한다 `() = runTest {
        //given
        val keywords = listOf("a", "b", "c")
        val isDarkBackgroundColor = fixture<Boolean>()
        val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else R.color.transparent_gray
        val event = OverlayTaskContract.Event.OnBackPressedClickFromSetting


        every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords
        every { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) } returns isDarkBackgroundColor
        val excepted = OverlayTaskContract.SideEffect.BackPressed(filterKeywordList = sharedPreferencesUtil.getFilterKeywordList(), backgroundColor = backgroundColor)

        //when
        viewModel.handleEvent(event)

        //then
        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `filter keyword 를 선택 하면 아래와 같이 sideEffect 및 state 를 변경 한다  `() = runTest {
        //given
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

        //when
        viewModel.handleEvent(event)

        //then
        verify { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) }

        assertEquals(exceptedSideEffect, viewModel.effect.first())
        assertEquals(exceptedState, viewModel.currentState)
    }

    @Test
    fun `clear event 가 발샏 하면 clear usecase 를 실행 한다`() = runTest {
        //given
        val event = OverlayTaskContract.Event.OnClearClick

        //when
        viewModel.handleEvent(event)

        //then
        coVerify { clearLogUseCase.run(Unit) }
    }

    @Test
    fun `delete event 가 발생 하면 delete Log UseCase 를 실행 한다 `() = runTest {
        //given
        val event = OverlayTaskContract.Event.DeleteLog

        //when
        viewModel.handleEvent(event)

        //then
        coVerify { deleteLogUseCase.run(Unit) }
    }

    @Test
    fun `검색 완료 버튼을 눌렀을 때, models 에서 keyword 와 일치 한다면 아래의 sideEffect 를 방출 한다 `() = runTest {
        //given
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

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `검색 완료 버튼을 눌렀을 때, models 에서 keyword 를 찾지 못한 다면, 아래의 sideEffect 를 방출 한다 `() = runTest {
        //given
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = "test 44"
        val event = OverlayTaskContract.Event.OnSearchClick(logUiModels, searchKeyword)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("List is empty.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `업 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 크다면 해당 인덱스를 찾아 sideEffect 를 방출 한다 `() = runTest {
        //given
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = "test 22" //1
        val curPosition = 0
        val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `업 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 작다면 해당 sideEffect 를 방출 한다 `() = runTest {
        //given
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

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `업 버튼을 눌렀을 때, 현재 포지션과 찾으려는 키워드의 인덱스가 같다면 error sideEffect 를 방출 한다 `() = runTest {
        //given
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = "test 22" //index 1
        val curPosition = 1
        val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `업 버튼을 눌렀을 때, 키워드가 공백이라면 NotFound error sideEffect 를 방출 한다`() = runTest {
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = ""
        val curPosition = 2
        val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("pls, input keyword")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `업 버튼을 눌렀을 때, 모델 데이터가 비어 있다면, NotFound error SideEffect 를 방출 한다`() = runTest {
        val logUiModels = emptyList<LogUiModel>()
        val searchKeyword = "test 11"
        val curPosition = 0
        val event = OverlayTaskContract.Event.OnPageUpClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `다운 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 크다면 해당 인덱스를 찾아 sideEffect 를 방출 한다 `() = runTest {
        //given
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

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `다운 버튼을 눌렀을 때, 현재 포지션 보다 찾으려는 키워드의 인덱스가 작다면 error sideEffect 를 방출 한다 `() = runTest {
        //given
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = "test 22" //index 1
        val curPosition = 2
        val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `다운 버튼을 눌렀을 때, 현재 포지션과 찾으려는 키워드의 인덱스가 같다면 error sideEffect 를 방출 한다 `() = runTest {
        //given
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = "test 22" //index 1
        val curPosition = 1
        val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `다운 버튼을 눌렀을 때, 키워드가 공백이라면 NotFound error sideEffect 를 방출 한다`() = runTest {
        val logUiModels = listOf(
            LogUiModel(content = "test 11", contentLevel = LogLevel.D),
            LogUiModel(content = "test 22", contentLevel = LogLevel.I),
            LogUiModel(content = "test 33", contentLevel = LogLevel.W)
        )
        val searchKeyword = ""
        val curPosition = 2
        val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("pls, input keyword")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `다운 버튼을 눌렀을 때, 모델 데이터가 비어 있다면, NotFound error SideEffect 를 방출 한다`() = runTest {
        val logUiModels = emptyList<LogUiModel>()
        val searchKeyword = "test 11"
        val curPosition = 0
        val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels, searchKeyword, curPosition)

        val excepted = OverlayTaskContract.SideEffect.Error.NotFoundLog("Not found or The end has been reached.")

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

    @Test
    fun `OnCollectLog 가 실행 되면 아래오 같은 sideEffect 를 방출 한다 `() = runTest {
        //given
        val keyword = fixture<String>()
        val event = OverlayTaskContract.Event.OnCollectLog(keyword)
        val logModels = fixture<List<LogModel>>()
        val logUiModels = logModels.map { LogUiModel(content = it.content, contentLevel = it.logLevel) }
        val excepted = OverlayTaskContract.SideEffect.FetchLogs(logUiModels)

        coEvery { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) } returns flowOf(logModels)

        //when
        viewModel.handleEvent(event)

        //then
        verify { getLogcatUseCase.invoke(GetLogcatUseCase.Params(keyword)) }

        assertEquals(excepted, viewModel.effect.first())
    }

}