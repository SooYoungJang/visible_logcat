package com.sooyoungjang.debuglibrary.domain.log.model

internal data class LogModel(
    val content: String,
    val logLevel: LogLevel
)

internal sealed class LogLevel(val containWord: String) {

    object V : LogLevel(this.V)
    object D : LogLevel(this.D)
    object I : LogLevel(this.I)
    object W : LogLevel(this.W)
    object E : LogLevel(this.E)
    object F : LogLevel(this.F)
    object S : LogLevel(this.S)

    companion object {
        private const val V: String = "V/"
        private const val D: String = "D/"
        private const val I: String = "I/"
        private const val W: String = "W/"
        private const val E: String = "E/"
        private const val F: String = "F/"
        private const val S: String = "S/"
    }
}