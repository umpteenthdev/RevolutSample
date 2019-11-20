package com.umpteenthdev.revolutsample.rates.adapters.timing

interface Metronome {

    /**
     * @param period Time between ticks in milliseconds
     */
    fun observe(period: Long, observer: Callback)

    fun release()

    interface Callback {
        fun onTick()
        fun onError(t: Throwable)
    }
}
