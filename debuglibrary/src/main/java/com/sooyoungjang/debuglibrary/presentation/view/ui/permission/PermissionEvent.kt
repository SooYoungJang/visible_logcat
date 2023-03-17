package com.sooyoungjang.debuglibrary.presentation.view.ui.permission

internal sealed interface PermissionEvent {
    object Allow: PermissionEvent
    object Deny: PermissionEvent
    object NeverDeny: PermissionEvent
}