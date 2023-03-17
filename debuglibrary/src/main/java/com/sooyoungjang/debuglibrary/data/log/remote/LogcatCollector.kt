package com.sooyoungjang.debuglibrary.data.log.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class LogcatCollector {

    fun clearLog() {
        ProcessBuilder()
            .command("logcat", "-c")
            .redirectErrorStream(true)
            .start()
    }

    fun collect(): Flow<String> {
        val pid = android.os.Process.myPid()
        val commandArray = mutableListOf("logcat", "-v", "long", "--pid=$pid")

        val process = ProcessBuilder()
            .command(commandArray)
            .redirectErrorStream(true)
            .start()

        return internalCollect(process)
    }

    private fun internalCollect(
        process: Process
    ): Flow<String> = flow {
        val sb = StringBuilder()
        val separator = System.lineSeparator()

        process
            .inputStream
            .bufferedReader()
            .useLines { lines ->
                lines.forEach { newLine ->
                    if (newLine.isBlank()) {
                        emit(sb.toString())
                        sb.clear()
                    } else {
                        sb.append(newLine).append(separator)
                    }
                }
            }
    }
        .flowOn(Dispatchers.IO)

}