package com.sooyoungjang.debuglibrary.domain.datastore

internal interface DataStoreRepository {

    fun getNeverSeeAgain(): Boolean
    suspend fun writeNeverSeeAgain(isNeverSeeAgain: Boolean)
}