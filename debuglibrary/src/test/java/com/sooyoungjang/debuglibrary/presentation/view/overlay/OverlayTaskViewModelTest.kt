package com.sooyoungjang.debuglibrary.presentation.view.overlay

import com.sooyoungjang.debuglibrary.domain.log.usecase.ClearLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.DeleteLogUseCase
import com.sooyoungjang.debuglibrary.domain.log.usecase.GetLogcatUseCase
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.OverlayTaskContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.overlay.viewmodel.OverlayTaskViewModel
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.SharedPreferencesUtil
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(MainCoroutineRule::class)
class OverlayTaskViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: OverlayTaskViewModel

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
        val event = OverlayTaskContract.Event.OnOpenClick

        val keywords = listOf("a","b","c")
        every { sharedPreferencesUtil.getFilterKeywordList() } returns keywords

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
        )

        viewModel.handleEvent(event)

        verify { sharedPreferencesUtil.getFilterKeywordList() }

        assertEquals(excepted, viewModel.currentState)
    }

    @Test
    fun `close event 가 요청되면 초기 상태를 방출 한다 `() {
        val event = OverlayTaskContract.Event.OnCloseClick

        val excepted = OverlayTaskContract.State.idle()

        viewModel.handleEvent(event)

        assertEquals(excepted, viewModel.currentState)
    }


}