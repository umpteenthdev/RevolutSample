package com.umpteenthdev.revolutsample.core.di

import android.content.Context
import com.umpteenthdev.revolutsample.core.outer.navigation.RootFragmentFactory
import com.umpteenthdev.revolutsample.core.outer.navigation.RootIntentFactory
import com.umpteenthdev.revolutsample.core.usecases.ExceptionMapper
import okhttp3.OkHttpClient

interface AppDependencies {

    val appContext: Context
    val okHttpClient: OkHttpClient
    val rootFragmentFactory: RootFragmentFactory
    val rootIntentFactory: RootIntentFactory
    val exceptionMapper: ExceptionMapper

    companion object {
        lateinit var instance: AppDependencies
    }
}
