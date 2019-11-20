package com.umpteenthdev.revolutsample.rates.usecases

import com.umpteenthdev.revolutsample.rates.entity.RateEntity

interface RatesGateway {
    suspend fun getRates(base: String?): Result

    sealed class Result {
        class Success(val result: RateEntity) : Result()
        class Error(val t: Throwable) : Result()
    }
}
