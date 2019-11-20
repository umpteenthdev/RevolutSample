package com.umpteenthdev.revolutsample.rates.usecases

interface CurrencyNamesGateway {

    suspend fun getCurrencyNames(): Result

    sealed class Result {
        /**
         * @param result A map where the key is currency symbol (e.g. 'USD'), the value is full currency name (e.g. 'United States Dollar')
         */
        class Success(val result: Map<String, String>) : Result()

        class Error(val t: Throwable) : Result()
    }
}
