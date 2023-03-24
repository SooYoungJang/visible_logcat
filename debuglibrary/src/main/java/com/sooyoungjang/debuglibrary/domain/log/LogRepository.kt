package com.sooyoungjang.debuglibrary.domain.log

import com.sooyoungjang.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.flow.Flow

internal interface LogRepository {
    fun getLogcatData(filterWord: String): Flow<List<LogModel>>
    fun clearLog()
    suspend fun deleteLogData()
}