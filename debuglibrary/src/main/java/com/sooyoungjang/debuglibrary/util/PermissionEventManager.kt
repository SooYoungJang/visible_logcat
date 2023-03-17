package com.sooyoungjang.debuglibrary.util

import com.sooyoungjang.debuglibrary.PermissionCallback
import com.sooyoungjang.debuglibrary.presentation.view.ui.permission.PermissionEvent
import org.greenrobot.eventbus.Subscribe

internal class PermissionEventManager(
    private val callback: PermissionCallback
) {

    @Subscribe
    fun permissionEventHandler(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Allow -> {
                callback.allow()
            }
            PermissionEvent.Deny -> {
                callback.deny()
            }
            PermissionEvent.NeverDeny -> {
                callback.neverDeny()
            }
        }
    }
}