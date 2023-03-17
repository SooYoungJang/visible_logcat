package com.eddy.debuglibrary.domain.datastore.usecase

import com.eddy.debuglibrary.domain.base.UseCase
import com.eddy.debuglibrary.domain.datastore.DataStoreRepository

internal class WriteDataStoreUseCase(
    private val dataStoreRepository: DataStoreRepository
) : UseCase<Unit, Boolean>() {

    public override suspend fun run(isNeverSeeAgain: Boolean) {
        dataStoreRepository.writeNeverSeeAgain(isNeverSeeAgain)
    }
}