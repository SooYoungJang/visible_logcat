package com.eddy.debuglibrary.data.datastore

import com.eddy.debuglibrary.data.datastore.local.DataStoreLocalDataSource
import com.eddy.debuglibrary.domain.datastore.DataStoreRepository

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