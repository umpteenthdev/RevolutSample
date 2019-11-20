package com.umpteenthdev.revolutsample.rates.adapters.cache

import java.util.*

interface RatesCache {
    var order: LinkedList<String>?
    var baseAmount: Double?
}
