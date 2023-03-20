package com.sooyoungjang.debuglibrary.presentation.view.ui.permission.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.debuglibrary.R
import com.sooyoungjang.debuglibrary.domain.datastore.usecase.WriteDataStoreUseCase
import com.sooyoungjang.debuglibrary.presentation.base.BaseViewModel
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionContract
import com.sooyoungjang.debuglibrary.util.ResourceProvider
import kotlinx.coroutines.launch


internal class PermissionViewModel(
    private val writeDataStoreUseCase: WriteDataStoreUseCase,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<PermissionContract.Event, PermissionContract.State, PermissionContract.SideEffect>() {
    override fun createIdleState(): PermissionContract.State {
        return PermissionContract.State.idle()
    }

    init {
        initData()
    }

    private fun initData() {
        setState {
            copy(
                title = resourceProvider.getString(R.string.request_permission),
                confirmTitle = resourceProvider.getString(R.string.confirm),
                cancelTitle = resourceProvider.getString(R.string.cancel),
                neverSeeAgainTitle = resourceProvider.getString(R.string.never_see_again),
            )
        }
    }

    override fun handleEvent(event: PermissionContract.Event) {
        when (event) {
            PermissionContract.Event.OnConfirmClick -> checkPermission()
            PermissionContract.Event.OnCancelClick -> cancel()
            PermissionContract.Event.OnNeverSeeAgainClick -> neverSeeAgain()
        }
    }

    private fun checkPermission() {
        setEffect { PermissionContract.SideEffect.CheckPermission }
    }

    private fun cancel() {
        setEffect { PermissionContract.SideEffect.Cancel }
    }

    private fun neverSeeAgain() {
        viewModelScope.launch {
            writeDataStoreUseCase.run(true)
            setEffect { PermissionContract.SideEffect.NeverSeeAgainCancel }
        }
    }
}