package com.umpteenthdev.revolutsample.outer.android

import android.app.Application
import android.os.SystemClock
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.umpteenthdev.revolutsample.BuildConfig
import com.umpteenthdev.revolutsample.core.di.AppDependencies
import com.umpteenthdev.revolutsample.core.entity.logd
import com.umpteenthdev.revolutsample.di.AppInjector
import com.umpteenthdev.revolutsample.di.DaggerAppComponent
import com.umpteenthdev.revolutsample.outer.performance.CommandLineHelper
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class App : Application() {

    @Inject lateinit var commandLineHelper: CommandLineHelper

    override fun onCreate() {
        val trace = FirebasePerformance.getInstance().newTrace("App_onCreate")
        val startTime = SystemClock.elapsedRealtime()
        trace.start()

        initDi()
        measureProcessStart(startTime, trace)
        super.onCreate()
        Paper.init(applicationContext)
        initCoroutines()

        trace.stop()
    }

    private fun initDi() {
        AppInjector.component = DaggerAppComponent.factory().create(applicationContext)
        AppDependencies.instance = AppInjector.component
        AppInjector.component.inject(this)
    }

    private fun initCoroutines() {
        if (!BuildConfig.DEBUG) return
        System.setProperty(kotlinx.coroutines.DEBUG_PROPERTY_NAME, kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON)
    }

    private fun measureProcessStart(startTime: Long, trace: Trace) {
        commandLineHelper.getProcessInfo()?.let { processInfo ->
            val processLaunchTime = startTime - processInfo.startTimeFromBootMillis
            logd("Process launch time = ${SimpleDateFormat("s 'sec.' SSS 'ms'", Locale.getDefault()).format(Date(processLaunchTime))}")
            trace.putMetric("Process_launch_time_ms", processLaunchTime)
        }
    }
}
