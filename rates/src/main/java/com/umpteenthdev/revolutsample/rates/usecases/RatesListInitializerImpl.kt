package com.umpteenthdev.revolutsample.rates.usecases

import com.umpteenthdev.revolutsample.rates.adapters.cache.RatesCache
import com.umpteenthdev.revolutsample.rates.adapters.flags.FlagImageUrlResolver
import javax.inject.Inject

class RatesListInitializerImpl @Inject constructor(
    private val currencyNamesGateway: CurrencyNamesGateway,
    private val flagImageUrlResolver: FlagImageUrlResolver,
    private val ratesCache: RatesCache
) : RatesListInitializer {

    override suspend fun initialize(): RatesListInitializer.Result {
        return try {
            when (val namesResult = currencyNamesGateway.getCurrencyNames()) {
                is CurrencyNamesGateway.Result.Success -> {
                    val urlsMap = namesResult.result.keys.associateWith { key ->
                        flagImageUrlResolver.getFlagImageUrl(key)
                    }
                    RatesListInitializer.Result.Success(
                        currencyNames = namesResult.result,
                        currencyIconUrls = urlsMap,
                        cachedBaseAmount = ratesCache.baseAmount,
                        cachedOrder = ratesCache.order
                    )
                }
                is CurrencyNamesGateway.Result.Error -> RatesListInitializer.Result.Error(namesResult.t)
            }
        } catch (t: Throwable) {
            RatesListInitializer.Result.Error(t)
        }
    }
}
