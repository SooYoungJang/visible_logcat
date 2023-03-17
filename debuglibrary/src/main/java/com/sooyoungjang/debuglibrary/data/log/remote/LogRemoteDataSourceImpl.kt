package com.sooyoungjang.debuglibrary.data.log.remote

import kotlinx.coroutines.flow.Flow

internal class LogRemoteDataSourceImpl(
    private val log: LogcatCollector
) : LogRemoteDataSource {
    override fun logDataCollect(): Flow<String> {
        return log.collect()
    }

    override fun clearLog() {
        log.clearLog()
    }
}