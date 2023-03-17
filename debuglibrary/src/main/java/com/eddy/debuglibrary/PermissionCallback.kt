package com.eddy.debuglibrary

internal interface PermissionCallback {
    fun allow()
    fun deny()
    fun neverDeny()
}