package com.umpteenthdev.revolutsample.core.outer.navigation

import android.content.Context
import android.content.Intent

interface IntentFactory {
    fun getIntent(context: Context, request: NavigationRequest): Intent?
}
