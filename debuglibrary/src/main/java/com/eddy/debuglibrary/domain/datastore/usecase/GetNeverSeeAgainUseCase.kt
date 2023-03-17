package com.eddy.debuglibrary.domain.datastore.usecase

import com.eddy.debuglibrary.domain.base.UseCase
import com.eddy.debuglibrary.domain.datastore.DataStoreRepository

internal class GetNeverSeeAgainUseCase(
    private val dataStoreRepository: DataStoreRepository
) : UseCase<Boolean, Unit>() {

    public override fun exec(params: Unit): Boolean {
        return dataStoreRepository.getNeverSeeAgain()
    }
}