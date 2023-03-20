package com.sooyoungjang.debuglibrary.presentation.view.ui.permission

import com.sooyoungjang.debuglibrary.presentation.base.UiEffect
import com.sooyoungjang.debuglibrary.presentation.base.UiEvent
import com.sooyoungjang.debuglibrary.presentation.base.UiState

internal class PermissionContract {
    sealed interface Event : UiEvent {
        object OnConfirmClick: Event
        object OnCancelClick: Event
        object OnNeverSeeAgainClick: Event
    }

    data class State(
        val title: String,
        val confirmTitle: String,
        val cancelTitle: String,
        val neverSeeAgainTitle: String,
    ) : UiState {
        companion object Factory {
            fun idle(): State {
                return State(
                    title = "",
                    confirmTitle = "",
                    cancelTitle = "",
                    neverSeeAgainTitle = ""
                )
            }
        }
    }

    sealed interface SideEffect : UiEffect {
        object CheckPermission: SideEffect
        object Cancel: SideEffect
        object NeverSeeAgainCancel: SideEffect
    }
}