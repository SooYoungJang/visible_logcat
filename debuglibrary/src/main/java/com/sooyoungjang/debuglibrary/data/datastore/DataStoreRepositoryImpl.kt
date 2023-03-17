package com.sooyoungjang.debuglibrary.data.datastore

import com.sooyoungjang.debuglibrary.data.datastore.local.DataStoreLocalDataSource
import com.sooyoungjang.debuglibrary.domain.datastore.DataStoreRepository

internal class DataStoreRepositoryImpl(
    private val dataStoreLocalDataSource: DataStoreLocalDataSource
): DataStoreRepository {

    override fun getNeverSeeAgain(): Boolean {
        return dataStoreLocalDataSource.readNeverSeeAgain()
    }

    override suspend fun writeNeverSeeAgain(isNeverSeeAgain: Boolean) {
        dataStoreLocalDataSource.write(isNeverSeeAgain)
    }

}