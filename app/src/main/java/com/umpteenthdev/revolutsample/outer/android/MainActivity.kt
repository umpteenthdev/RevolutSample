package com.umpteenthdev.revolutsample.outer.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.umpteenthdev.revolutsample.core.outer.navigation.RootIntentFactory
import com.umpteenthdev.revolutsample.di.AppInjector
import com.umpteenthdev.revolutsample.rates.api.navigation.RatesListRequest
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var intentFactory: RootIntentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AppInjector.component.inject(this)
        super.onCreate(savedInstanceState)
        val intent = requireNotNull(intentFactory.getIntent(this, RatesListRequest()))
        startActivity(intent)
    }
}
