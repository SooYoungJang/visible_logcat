package com.sooyoungjang.debuglibrary.presentation.view.model


public data class LogForm(
    val color: Int,
    val logLevel: LogLevel
)

public enum class LogLevel {
    /**
     * Align the arrow position depending on the balloon popup body.
     *
     * If [Balloon.Builder.arrowPosition] is 0.5, the arrow will be located in the middle of the tooltip.
     */
    V,
    D,
    I,
    W,
    E,
    F,
    S
}
