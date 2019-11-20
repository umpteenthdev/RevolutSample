package com.umpteenthdev.revolutsample.outer.navigation

import android.content.Context
import android.content.Intent
import com.umpteenthdev.revolutsample.core.outer.navigation.IntentFactory
import com.umpteenthdev.revolutsample.core.outer.navigation.NavigationRequest
import com.umpteenthdev.revolutsample.core.outer.navigation.RootIntentFactory
import com.umpteenthdev.revolutsample.rates.outer.navigation.RatesIntentFactory
import javax.inject.Inject

class RootIntentFactoryImpl @Inject constructor() : RootIntentFactory {

    private val featureFactoryList: List<IntentFactory> = listOf(
        RatesIntentFactory()
    )

    override fun getIntent(context: Context, request: NavigationRequest): Intent? {

        for (factory in featureFactoryList) {
            val intent = factory.getIntent(context, request)

            if (intent != null) {
                return intent
            }
        }

        return null
    }
}
