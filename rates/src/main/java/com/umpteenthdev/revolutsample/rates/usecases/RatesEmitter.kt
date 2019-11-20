package com.umpteenthdev.revolutsample.rates.usecases

import com.umpteenthdev.revolutsample.rates.entity.RateEntity

interface RatesEmitter {

    fun setBase(base: String?)

    fun observe(observer: Callback)

    fun remove(observer: Callback)

    fun clear()

    interface Callback {
        fun onNewRates(rates: RateEntity)
        fun onError(t: Throwable)
    }
}
