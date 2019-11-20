package com.umpteenthdev.revolutsample.rates.outer.navigation

import android.content.Context
import android.content.Intent
import com.umpteenthdev.revolutsample.core.outer.navigation.IntentFactory
import com.umpteenthdev.revolutsample.core.outer.navigation.NavigationRequest
import com.umpteenthdev.revolutsample.rates.api.navigation.RatesListRequest
import com.umpteenthdev.revolutsample.rates.outer.android.RatesActivity

class RatesIntentFactory : IntentFactory {

    override fun getIntent(context: Context, request: NavigationRequest): Intent? = when (request) {
        is RatesListRequest -> RatesActivity.getStartIntent(context)
        else -> null
    }
}
