package com.umpteenthdev.revolutsample.rates.outer.flags

import com.umpteenthdev.revolutsample.rates.adapters.flags.FlagImageUrlResolver
import javax.inject.Inject

private const val BASE_URL = "https://raw.githubusercontent.com/umpteenthdev/RevolutSample/master/flags/"

class GithubFlagImageUrlResolver @Inject constructor() : FlagImageUrlResolver {

    override fun getFlagImageUrl(currencySymbol: String): String {
        return "$BASE_URL$currencySymbol.png"
    }
}
