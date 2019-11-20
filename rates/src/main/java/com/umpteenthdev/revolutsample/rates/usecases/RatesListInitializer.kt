package com.umpteenthdev.revolutsample.rates.usecases

import java.util.*

interface RatesListInitializer {

    suspend fun initialize(): Result

    sealed class Result {
        /**
         * @param currencyNames Currency symbol to full currency name. E.g. 'RUB' to 'Russian Rouble'
         * @param currencyIconUrls Currency symbol to url of the correspondent icon. E.g. 'AUD' to 'https://raw.githubusercontent.com/umpteenthdev/RevolutSample/master/flags/AUD.png'
         * @param cachedBaseAmount Last inputted amount
         * @param cachedOrder Order of currencies that user previously set
         */
        class Success(
            val currencyNames: Map<String, String>,
            val currencyIconUrls: Map<String, String>,
            val cachedBaseAmount: Double?,
            val cachedOrder: LinkedList<String>?
        ) : Result()

        class Error(val t: Throwable) : Result()
    }
}
