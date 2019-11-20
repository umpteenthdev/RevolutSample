package com.umpteenthdev.revolutsample.di

import android.content.Context
import com.umpteenthdev.revolutsample.core.di.AppDependencies
import com.umpteenthdev.revolutsample.outer.android.MainActivity
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [AppModule::class])
interface AppComponent : AppDependencies {

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance appContext: Context
        ): AppComponent
    }
}
