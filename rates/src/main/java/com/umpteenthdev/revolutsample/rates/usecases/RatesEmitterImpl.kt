package com.umpteenthdev.revolutsample.rates.usecases

import com.umpteenthdev.revolutsample.core.entity.log
import com.umpteenthdev.revolutsample.core.entity.logd
import com.umpteenthdev.revolutsample.core.entity.requireAllBranches
import com.umpteenthdev.revolutsample.rates.adapters.timing.Metronome
import com.umpteenthdev.revolutsample.rates.entity.RateEntity
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RatesEmitterImpl @Inject constructor(
    private val metronome: Metronome,
    private val ratesGateway: RatesGateway
) : RatesEmitter, CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        SupervisorJob() + Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    }
    private val observers: MutableSet<RatesEmitter.Callback> = mutableSetOf()
    private val metronomeObserver: Metronome.Callback = initMetronomeObserver()
    @Volatile private var base: String? = null
    @Volatile private var currentJob: Job? = null

    override fun setBase(base: String?) {
        log("Set base: $base")
        if (this.base == base) return

        currentJob?.cancel()
        this.base = base
    }

    override fun observe(observer: RatesEmitter.Callback) {
        synchronized(observers) {
            logd("Add observer: $observer")
            val shouldStart = observers.isEmpty()
            observers.add(observer)
            if (shouldStart) {
                start()
            }
        }
    }

    override fun remove(observer: RatesEmitter.Callback) {
        synchronized(observers) {
            logd("Remove observer: $observer")
            observers.remove(observer)
            if (observers.isEmpty()) {
                stop()
            }
        }
    }

    override fun clear() {
        synchronized(observers) {
            logd("Clear observers")
            observers.clear()
            stop()
        }
    }

    private fun start() {
        log("Start emitting")
        metronome.observe(
            period = 1_000,
            observer = metronomeObserver
        )
    }

    private fun stop() {
        log("Stop emitting")
        metronome.release()
    }

    private fun initMetronomeObserver(): Metronome.Callback {
        return object : Metronome.Callback {
            override fun onTick() {
                onMetronomeTick()
            }

            override fun onError(t: Throwable) {
                this@RatesEmitterImpl.onError(t)
            }
        }
    }

    private fun onMetronomeTick() {
        if (currentJob?.isActive == true) return

        currentJob = launch {
            val response = ratesGateway.getRates(base)
            when (response) {
                is RatesGateway.Result.Success -> onNewRates(response.result)
                is RatesGateway.Result.Error -> onError(response.t)
            }.requireAllBranches()
        }
    }

    private fun onError(t: Throwable) {
        if (t is CancellationException) return

        synchronized(observers) {
            for (observer in observers) {
                observer.onError(t)
            }
            observers.clear()
            metronome.release()
        }
    }

    private fun onNewRates(rates: RateEntity) {
        synchronized(observers) {
            for (observer in observers) {
                observer.onNewRates(rates)
            }
        }
    }
}
