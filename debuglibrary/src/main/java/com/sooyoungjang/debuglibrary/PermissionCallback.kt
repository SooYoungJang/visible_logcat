package com.sooyoungjang.debuglibrary

internal interface PermissionCallback {
    fun allow()
    fun deny()
    fun neverDeny()
}