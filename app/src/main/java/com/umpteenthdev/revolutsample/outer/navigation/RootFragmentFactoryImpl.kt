package com.umpteenthdev.revolutsample.outer.navigation

import androidx.fragment.app.Fragment
import com.umpteenthdev.revolutsample.core.outer.navigation.FragmentFactory
import com.umpteenthdev.revolutsample.core.outer.navigation.NavigationRequest
import com.umpteenthdev.revolutsample.core.outer.navigation.RootFragmentFactory
import com.umpteenthdev.revolutsample.rates.outer.navigation.RatesFragmentFactory
import javax.inject.Inject

class RootFragmentFactoryImpl @Inject constructor() : RootFragmentFactory {

    private val featureFactoryList: List<FragmentFactory> = listOf(
        RatesFragmentFactory()
    )

    override fun getFragment(request: NavigationRequest): Fragment? {

        for (factory in featureFactoryList) {
            val fragment = factory.getFragment(request)

            if (fragment != null) {
                return fragment
            }
        }

        return null
    }
}
