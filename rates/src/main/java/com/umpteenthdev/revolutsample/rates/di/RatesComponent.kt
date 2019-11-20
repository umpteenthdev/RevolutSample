package com.umpteenthdev.revolutsample.rates.di

import com.umpteenthdev.revolutsample.core.di.AppDependencies
import com.umpteenthdev.revolutsample.rates.adapters.ui.RatesViewModel
import com.umpteenthdev.revolutsample.rates.outer.android.RatesFragment
import dagger.Component

@RatesScope
@Component(modules = [RatesModule::class], dependencies = [AppDependencies::class])
internal interface RatesComponent {

    fun inject(ratesFragment: RatesFragment)
    fun inject(ratesViewModel: RatesViewModel)

    @Component.Factory
    interface Factory {
        fun create(
            appDependencies: AppDependencies
        ): RatesComponent
    }
}
