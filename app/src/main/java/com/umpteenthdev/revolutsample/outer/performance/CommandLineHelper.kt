package com.umpteenthdev.revolutsample.outer.performance

interface CommandLineHelper {

    fun getProcessInfo(): ProcessInfo?

    data class ProcessInfo(
        val startTimeFromBootMillis: Long
    )
}
