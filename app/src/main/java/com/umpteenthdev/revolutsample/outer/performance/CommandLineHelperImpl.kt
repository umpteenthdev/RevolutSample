package com.umpteenthdev.revolutsample.outer.performance

import android.app.ActivityManager
import android.content.Context
import android.system.Os
import android.system.OsConstants
import com.umpteenthdev.revolutsample.core.entity.logd
import java.io.BufferedReader
import java.io.FileReader
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CommandLineHelperImpl @Inject constructor(
    appContext: Context
) : CommandLineHelper {

    private val activityManager: ActivityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val packageName: String = appContext.packageName

    override fun getProcessInfo(): CommandLineHelper.ProcessInfo? {
        val pid = getProcessId(packageName) ?: return null
        return ProcPidStat.getProcessInfo(pid)

    }

    private fun getProcessId(processName: String): Int? = activityManager.runningAppProcesses
        .firstOrNull { info -> info.pkgList.contains(processName) }
        ?.pid

    private object ProcPidStat {

        private const val START_TIME_INDEX = 21

        fun getProcessInfo(pid: Int): CommandLineHelper.ProcessInfo? = try {
            val reader = BufferedReader(FileReader("/proc/$pid/stat"))
            val fields = reader.use { it.readLine() }.split(" ")

            val processStartClockTicks = fields[START_TIME_INDEX].toLong()
            val clockTicksCoefficient = Os.sysconf(OsConstants._SC_CLK_TCK)
            val millis = processStartClockTicks / clockTicksCoefficient * TimeUnit.SECONDS.toMillis(1)

            CommandLineHelper.ProcessInfo(
                startTimeFromBootMillis = millis
            )
        } catch (t: Throwable) {
            logd(t)
            null
        }
    }

}
