package com.eddy.debuglibrary.data.log.remote

import kotlinx.coroutines.flow.Flow

internal interface LogRemoteDataSource {
    fun logDataCollect(): Flow<String>
    fun clearLog()
}