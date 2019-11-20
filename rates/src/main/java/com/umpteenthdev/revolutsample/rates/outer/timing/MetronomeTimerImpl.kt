package com.umpteenthdev.revolutsample.rates.outer.timing

import com.umpteenthdev.revolutsample.core.entity.log
import com.umpteenthdev.revolutsample.rates.adapters.timing.Metronome
import java.util.*
import javax.inject.Inject

class MetronomeTimerImpl @Inject constructor() : Metronome {

    private val timer: Timer = Timer("MetronomeTimer", true)
    private var task: TimerTask? = null
    private var observer: Metronome.Callback? = null

    override fun observe(period: Long, observer: Metronome.Callback) {
        this.observer = observer
        start(period)
    }

    override fun release() {
        log("Metronome release")
        task?.cancel()
        task = null
    }

    private fun start(period: Long) {
        release()

        log("Metronome start. Period: $period")
        val task = object : TimerTask() {
            override fun run() {
                observer?.onTick()
            }
        }
        this.task = task
        timer.schedule(task, 0, period)
    }
}
