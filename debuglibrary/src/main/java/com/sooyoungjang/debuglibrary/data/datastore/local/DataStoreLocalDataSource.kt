package com.sooyoungjang.debuglibrary.data.datastore.local

internal interface DataStoreLocalDataSource {
    fun readNeverSeeAgain() : Boolean
    suspend fun <T>write(param: T)
    suspend fun <T>clear(param: T)
}