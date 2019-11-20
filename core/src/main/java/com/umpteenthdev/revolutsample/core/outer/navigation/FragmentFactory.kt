package com.umpteenthdev.revolutsample.core.outer.navigation

import androidx.fragment.app.Fragment

interface FragmentFactory {
    fun getFragment(request: NavigationRequest): Fragment?
}
