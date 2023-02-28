package com.eddy.debuglibrary.data.log

import com.eddy.debuglibrary.data.log.entity.LogEntity
import com.eddy.debuglibrary.data.log.local.LogLocalDataSource
import com.eddy.debuglibrary.data.log.remote.LogRemoteDataSource
import com.eddy.debuglibrary.domain.log.LogRepository
import com.eddy.debuglibrary.domain.log.model.LogLevel
import com.eddy.debuglibrary.domain.log.model.LogModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

internal class LogRepositoryImpl(
    private val remote: LogRemoteDataSource,
    private val local: LogLocalDataSource
) : LogRepository {

    private val exceptChar =
        listOf(
            "WifiMulticast", "WifiHW", "MtpService", "PushClient", "ViewRootImpl", "InputTransport", "InputMethodManager", "mali|TextView", "activityThread", "TrafficStats",
            "tagSocket", "ConversionRepoter", "nativeloader", "DecorView", "BLASTBufferQueue", "OpenGLRenderer", "chromium", "CCodec", "Codec2", "ReflectedParamUpdater",
            "ColorUtils", "MediaCodec", "SurfaceUtils", "CCodecBufferChannel", "BufferQueueProducer", "cr_MediaCodecBridge", "BufferPoolAccessor","beginning of main", "StudioTransport",
            "SmartClipRemoteRequestDispatcher","hw-BpHwBinder","choreogra"
        )

    override fun getLogcatData(filterWord: String): Flow<List<LogModel>> {
        return remote.logDataCollect()
            .filterNot { exceptChar.any { char -> it.contains(char) } }
            .filter { it.contains(filterWord) }
            .map {
                when {
                    it.contains(LogLevel.V.containWord) -> { LogEntity(content = it, level = LogLevel.V.containWord) }
                    it.contains(LogLevel.D.containWord) -> { LogEntity(content = it, level = LogLevel.D.containWord) }
                    it.contains(LogLevel.I.containWord) -> { LogEntity(content = it, level = LogLevel.I.containWord) }
                    it.contains(LogLevel.W.containWord) -> { LogEntity(content = it, level = LogLevel.W.containWord) }
                    it.contains(LogLevel.E.containWord) -> { LogEntity(content = it, level = LogLevel.E.containWord) }
                    it.contains(LogLevel.F.containWord) -> { LogEntity(content = it, level = LogLevel.F.containWord) }
                    it.contains(LogLevel.S.containWord) -> { LogEntity(content = it, level = LogLevel.S.containWord) }
                    else -> { LogEntity(content = it, level = LogLevel.S.containWord) }
                }
            }.map { local.insertLog(it) }
            .flatMapConcat { local.getAllLog() }
            .map {
                it.map {
                    when {
                        it.level.contains(LogLevel.V.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.V) }
                        it.level.contains(LogLevel.D.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.D) }
                        it.level.contains(LogLevel.I.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.I) }
                        it.level.contains(LogLevel.W.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.W) }
                        it.level.contains(LogLevel.E.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.E) }
                        it.level.contains(LogLevel.F.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.F) }
                        it.level.contains(LogLevel.S.containWord) -> { LogModel(content = it.content, logLevel = LogLevel.S) }
                        else -> { LogModel(content = it.content, logLevel = LogLevel.E) }
                    }
                }
            }
            .flowOn(Dispatchers.IO)
    }


    override fun clearLog() {
        remote.clearLog()
    }

    override suspend fun deleteLogData() {
        local.deleteAllLog()
        remote.clearLog()
    }
}