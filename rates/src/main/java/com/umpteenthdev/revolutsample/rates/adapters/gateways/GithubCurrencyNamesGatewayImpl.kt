package com.umpteenthdev.revolutsample.rates.adapters.gateways

import com.umpteenthdev.revolutsample.rates.adapters.api.RatesApi
import com.umpteenthdev.revolutsample.rates.usecases.CurrencyNamesGateway
import javax.inject.Inject

class GithubCurrencyNamesGatewayImpl @Inject constructor(
    private val ratesApi: RatesApi
) : CurrencyNamesGateway {

    override suspend fun getCurrencyNames(): CurrencyNamesGateway.Result {
        return try {
            CurrencyNamesGateway.Result.Success(ratesApi.getCurrenciesNames())
        } catch (t: Throwable) {
            CurrencyNamesGateway.Result.Error(t)
        }
    }
}
