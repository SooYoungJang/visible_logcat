package com.sooyoungjang.debuglibrary.domain.log.usecase

import com.sooyoungjang.debuglibrary.domain.base.UseCase
import com.sooyoungjang.debuglibrary.domain.log.LogRepository
import com.sooyoungjang.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.flow.Flow

internal class GetLogcatUseCase(
    private val log: LogRepository
) : UseCase<List<LogModel>, GetLogcatUseCase.Params>() {

    public override fun invoke(params: Params): Flow<List<LogModel>> {
        val filterWord = if(params.searchKeyWord == "normal") "" else params.searchKeyWord
        return log.getLogcatData(filterWord)
    }

    data class Params(
        val searchKeyWord: String
    )
}