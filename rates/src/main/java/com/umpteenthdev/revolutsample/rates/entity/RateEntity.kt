package com.umpteenthdev.revolutsample.rates.entity

/**
 * @param rates A map where the key is currency symbol (e.g. 'RUB'), the value is rate (e.g. '65.7')
 */
data class RateEntity(
    val baseCurrencySymbol: String,
    val rates: Map<String, Double>
)
