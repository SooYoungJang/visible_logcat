package com.sooyoungjang.debuglibrary.presentation.view.overlay

import com.appmattus.kotlinfixture.kotlinFixture
import com.example.debuglibrary.R
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
        val keywords = listOf("a","b","c")
        val isDarkBackgroundColor = fixture<Boolean>()
        val backgroundColor = if (isDarkBackgroundColor) R.color.default_app_color else  R.color.transparent_gray

        every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords
        every { sharedPreferencesUtil.getBoolean(Constants.SharedPreferences.EDDY_SETTING_BACKGROUND) } returns isDarkBackgroundColor

        val excepted = OverlayTaskContract.State.idle().copy(
            expandView = true,
            setting = true,
            keywordTitle = true,
            filterKeyword = true,
            filterKeywordList = keywords,
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
    fun `setting activity 에서 backPress 를 누르면 filterKeywordList 상태를  변경 한다 `() {
        //given
        val keyword = fixture<String>()
        val keywords = listOf("a","b","c")
        val event = OverlayTaskContract.Event.OnBackPressedClickFromSetting(keyword)

        every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords
        val excepted = OverlayTaskContract.State.idle().copy(filterKeywordList = sharedPreferencesUtil.getFilterKeywordList())

        //when
        viewModel.handleEvent(event)

        //then
        assertEquals(excepted, viewModel.currentState)
    }

    @Test
    fun `filter keyword 를 선택 아래와 같이 sideEffect 를 발행 한다  `() = runTest {
        //given
        val keyword = fixture<String>()
        val event = OverlayTaskContract.Event.OnKeywordItemClick(keyword)
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
    fun `search 버튼을 누르면 아래와 같은 상태로 변경 한다 ` () {
        //given
        val logUiModels = fixture<List<LogUiModel>>()
        val event = OverlayTaskContract.Event.OnPageDownClick(logUiModels)

        val excepted = OverlayTaskContract.State.idle().copy(
            scrollPosition =
        )

        //when
        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.effect.first())
    }

}