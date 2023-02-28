package com.eddy.debuglibrary.data.log.local

import com.eddy.debuglibrary.data.log.entity.LogEntity
import com.eddy.debuglibrary.data.log.local.dao.LogDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class LogLocalDataSourceImpl(
    private val logDao: LogDao
) : LogLocalDataSource {

    override fun insertLog(logEntity: LogEntity) {
        logDao.insertLog(logEntity)
    }

    override fun getAllLog(): Flow<List<LogEntity>> = flow {
        emit(logDao.getAllLog())
    }

    override fun insertAllLog(logEntities: List<LogEntity>) {
        logDao.insertAllLog(logEntities)
    }

    override suspend fun deleteAllLog() {
        logDao.deleteAllLog()
    }

}