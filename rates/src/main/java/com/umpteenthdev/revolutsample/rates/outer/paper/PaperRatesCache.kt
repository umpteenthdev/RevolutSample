package com.umpteenthdev.revolutsample.rates.outer.paper

import com.umpteenthdev.revolutsample.rates.adapters.cache.RatesCache
import io.paperdb.Paper
import java.util.*
import javax.inject.Inject

private const val BOOK_NAME = "rates_rates_cache_book"
private const val KEY_ORDER = "order"
private const val KEY_BASE_AMOUNT = "base_amount"

class PaperRatesCache @Inject constructor() : RatesCache {

    @set:Synchronized
    override var order: LinkedList<String>?
        get() = Paper.book(BOOK_NAME).read(KEY_ORDER)
        set(value) {
            Paper.book(BOOK_NAME).write(KEY_ORDER, value)
        }

    @set:Synchronized
    override var baseAmount: Double?
        get() = Paper.book(BOOK_NAME).read(KEY_BASE_AMOUNT)
        set(value) {
            Paper.book(BOOK_NAME).write(KEY_BASE_AMOUNT, value)
        }
}
