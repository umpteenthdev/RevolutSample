package com.umpteenthdev.revolutsample.rates.outer.navigation

import androidx.fragment.app.Fragment
import com.umpteenthdev.revolutsample.core.outer.navigation.FragmentFactory
import com.umpteenthdev.revolutsample.core.outer.navigation.NavigationRequest
import com.umpteenthdev.revolutsample.rates.api.navigation.RatesListRequest
import com.umpteenthdev.revolutsample.rates.outer.android.RatesFragment

class RatesFragmentFactory : FragmentFactory {

    override fun getFragment(request: NavigationRequest): Fragment? = when (request) {
        is RatesListRequest -> RatesFragment.newInstance()
        else -> null
    }
}
