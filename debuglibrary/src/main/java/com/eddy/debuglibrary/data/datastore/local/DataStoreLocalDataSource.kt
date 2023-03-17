package com.eddy.debuglibrary.data.datastore.local

import kotlinx.coroutines.flow.Flow

internal interface DataStoreLocalDataSource {
    fun readNeverSeeAgain() : Boolean
    suspend fun <T>write(param: T)
    suspend fun <T>clear(param: T)
}