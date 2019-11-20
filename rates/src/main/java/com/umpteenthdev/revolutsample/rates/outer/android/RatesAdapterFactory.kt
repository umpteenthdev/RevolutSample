package com.umpteenthdev.revolutsample.rates.outer.android

interface RatesAdapterFactory {
    fun create(callback: RatesAdapter.Callback): RatesAdapter
}
