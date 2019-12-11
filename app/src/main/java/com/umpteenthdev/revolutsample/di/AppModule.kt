package com.umpteenthdev.revolutsample.di

import com.umpteenthdev.revolutsample.BuildConfig
import com.umpteenthdev.revolutsample.core.entity.logd
import com.umpteenthdev.revolutsample.core.outer.navigation.RootFragmentFactory
import com.umpteenthdev.revolutsample.core.outer.navigation.RootIntentFactory
import com.umpteenthdev.revolutsample.core.usecases.ExceptionMapper
import com.umpteenthdev.revolutsample.outer.navigation.RootFragmentFactoryImpl
import com.umpteenthdev.revolutsample.outer.navigation.RootIntentFactoryImpl
import com.umpteenthdev.revolutsample.outer.performance.CommandLineHelper
import com.umpteenthdev.revolutsample.outer.performance.CommandLineHelperImpl
import com.umpteenthdev.revolutsample.usecases.ExceptionMapperImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
abstract class AppModule {

    @Binds
    @AppScope
    abstract fun rootFragmentFactory(impl: RootFragmentFactoryImpl): RootFragmentFactory

    @Binds
    @AppScope
    abstract fun rootIntentFactory(impl: RootIntentFactoryImpl): RootIntentFactory

    @Binds
    @AppScope
    abstract fun exceptionMapper(impl: ExceptionMapperImpl): ExceptionMapper

    @Binds
    @AppScope
    abstract fun commandLineHelper(impl: CommandLineHelperImpl): CommandLineHelper

    @Module
    companion object {

        @Provides
        @JvmStatic
        @AppScope
        fun httpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addNetworkInterceptor(
                    HttpLoggingInterceptor(
                        object : HttpLoggingInterceptor.Logger {
                            override fun log(message: String) {
                                logd(
                                    tag = "NETWORK",
                                    message = message
                                )
                            }
                        }
                    ).also {
                        it.level = if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BASIC
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                    }
                )
                .build()
        }
    }
}
