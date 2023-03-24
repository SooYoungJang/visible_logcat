package com.sooyoungjang.debuglibrary.presentation.view.permission

import com.appmattus.kotlinfixture.kotlinFixture
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.WriteDataStoreUseCase
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionContract
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.viewmodel.PermissionViewModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.setting.viewmodel.SettingViewModel
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import com.sooyoungjang.debuglibrary.util.di.MainCoroutineRule
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class PermissionViewModelTest : BehaviorSpec({

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
    val resourceProvider: ResourceProvider = mockk(relaxed = true) {
        every { getString(R.string.request_permission) } returns fixture()
        every { getString(R.string.confirm) } returns fixture()
        every { getString(R.string.cancel) } returns fixture()
        every { getString(R.string.never_see_again) } returns fixture()
    }

    val writeDataStoreUseCase: WriteDataStoreUseCase = mockk(relaxed = true)

    Given("viewModel 이 생성 되고") {
        val viewModel = PermissionViewModel(writeDataStoreUseCase, resourceProvider)
        val idleState = viewModel.createIdleState()

        When("대기 상태 일 때") {
            Then("State 값은 Idle 상태 이다.") {
                assertEquals(PermissionContract.State.idle(), idleState)
            }
        }

        When("대기 상태 이후에") {
            val excepted = PermissionContract.State.idle().copy(
                title = resourceProvider.getString(R.string.request_permission),
                confirmTitle = resourceProvider.getString(R.string.confirm),
                cancelTitle = resourceProvider.getString(R.string.cancel),
                neverSeeAgainTitle = resourceProvider.getString(R.string.never_see_again)
            )
            Then("디폴트 값으로 상태를 변경 한다.") {
                assertEquals(excepted, viewModel.currentState)
            }
        }

        When("권한 확인을 클릭 하면") {
            val event = PermissionContract.Event.OnConfirmClick
            val expected = PermissionContract.SideEffect.CheckPermission

            viewModel.handleEvent(event)
            Then("권한 창으로 이동하는 SideEffect 를 방출한다") {
                assertEquals(expected, viewModel.effect.first())
            }
        }

        When("권한 취소를 클릭 하면") {
            val event = PermissionContract.Event.OnCancelClick
            val expected = PermissionContract.SideEffect.Cancel

            viewModel.handleEvent(event)
            Then("SideEffect 를 방출 한다") {
                assertEquals(expected, viewModel.effect.first())
            }
        }

        When("권한 영구 거절을 클릭 하면") {
            val event = PermissionContract.Event.OnNeverSeeAgainClick
            val expected = PermissionContract.SideEffect.NeverSeeAgainCancel

            viewModel.handleEvent(event)
            Then("데이터를 저장하고, 취소 SideEffect 를 방출 한다") {
                coVerify { writeDataStoreUseCase.run(true) }
                assertEquals(expected, viewModel.effect.first())
            }
        }
    }

})