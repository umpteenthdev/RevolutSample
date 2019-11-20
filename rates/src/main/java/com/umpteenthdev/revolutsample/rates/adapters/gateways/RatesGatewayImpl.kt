package com.umpteenthdev.revolutsample.rates.adapters.gateways

import com.umpteenthdev.revolutsample.rates.adapters.api.RatesApi
import com.umpteenthdev.revolutsample.rates.entity.RateEntity
import com.umpteenthdev.revolutsample.rates.usecases.RatesGateway
import javax.inject.Inject

class RatesGatewayImpl @Inject constructor(private val api: RatesApi) : RatesGateway {

    override suspend fun getRates(base: String?): RatesGateway.Result {
        return try {
            val response = api.getRates(base)
            val result = mapRates(response)
            RatesGateway.Result.Success(result)
        } catch (t: Throwable) {
            RatesGateway.Result.Error(t)
        }
    }

    private fun mapRates(response: RatesApi.Rates): RateEntity {
        return RateEntity(
            baseCurrencySymbol = response.base,
            rates = response.rates
        )
    }
}
