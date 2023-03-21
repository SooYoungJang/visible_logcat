package com.sooyoungjang.debuglibrary.presentation.view.permission

import com.appmattus.kotlinfixture.kotlinFixture
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.WriteDataStoreUseCase
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.viewmodel.PermissionViewModel
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.mockk.coVerify
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
class PermissionViewModelTest {

    val fixture = kotlinFixture()
    private lateinit var viewModel: PermissionViewModel
    private val resourceProvider: ResourceProvider = mockk(relaxed = true) {
        every { getString(R.string.request_permission) } returns fixture()
        every { getString(R.string.confirm) } returns fixture()
        every { getString(R.string.cancel) } returns fixture()
        every { getString(R.string.never_see_again) } returns fixture()
    }

    private val writeDataStoreUseCase: WriteDataStoreUseCase = mockk(relaxed = true)

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @BeforeEach
    fun setup() {
        viewModel = PermissionViewModel(writeDataStoreUseCase, resourceProvider)
    }

    @Test
    fun `viewModel 의 초기 State 값은 idle 이다 `() {
        val idleState = viewModel.createIdleState()

        assertEquals(PermissionContract.State.idle(), idleState)
    }

    @Test
    fun `viewModel이 init 되고 난 후에는 디폴트 값을 넣어 준다 `() {
        viewModel.createIdleState()

        val excepted = PermissionContract.State.idle().copy(
            title = resourceProvider.getString(R.string.request_permission),
            confirmTitle = resourceProvider.getString(R.string.confirm),
            cancelTitle = resourceProvider.getString(R.string.cancel),
            neverSeeAgainTitle = resourceProvider.getString(R.string.never_see_again)
        )

        assertEquals(excepted, viewModel.currentState)
    }

    @Test
    fun ` 권한 확인을 누른 다면 권한 창으로 이동하는 SideEffect 를 방출한다 `() = runTest {
        val event = PermissionContract.Event.OnConfirmClick
        val expected = PermissionContract.SideEffect.CheckPermission

        viewModel.handleEvent(event)

        assertEquals(expected, viewModel.effect.first())
    }

    @Test
    fun `취소를 하게 되면 취소 SideEffect 를 방출 한다`() = runTest {
        val event = PermissionContract.Event.OnCancelClick
        val expected = PermissionContract.SideEffect.Cancel

        viewModel.handleEvent(event)

        assertEquals(expected, viewModel.effect.first())
    }

    @Test
    fun `권한을 영구 거절 하면 해당 데이터를 저장하고, 취소 SideEffect 를 방출 한다 `() = runTest {
        val event = PermissionContract.Event.OnNeverSeeAgainClick
        val expected = PermissionContract.SideEffect.NeverSeeAgainCancel

        viewModel.handleEvent(event)

        coVerify { writeDataStoreUseCase.run(true) }
        assertEquals(expected, viewModel.effect.first())
    }


}