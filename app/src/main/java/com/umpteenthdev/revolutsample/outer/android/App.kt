package com.umpteenthdev.revolutsample.outer.android

import android.app.Application
import com.umpteenthdev.revolutsample.BuildConfig
import com.umpteenthdev.revolutsample.core.di.AppDependencies
import com.umpteenthdev.revolutsample.di.AppInjector
import com.umpteenthdev.revolutsample.di.DaggerAppComponent
import io.paperdb.Paper

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppInjector.component = DaggerAppComponent.factory().create(applicationContext)
        AppDependencies.instance = AppInjector.component

        Paper.init(applicationContext)

        if (BuildConfig.DEBUG) {
            System.setProperty(kotlinx.coroutines.DEBUG_PROPERTY_NAME, kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON)
        }
    }
}
