package com.umpteenthdev.revolutsample.rates.adapters.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.umpteenthdev.revolutsample.core.di.AppDependencies
import com.umpteenthdev.revolutsample.core.entity.RevException
import com.umpteenthdev.revolutsample.core.entity.log
import com.umpteenthdev.revolutsample.core.entity.requireAllBranches
import com.umpteenthdev.revolutsample.core.usecases.ExceptionMapper
import com.umpteenthdev.revolutsample.rates.adapters.cache.RatesCache
import com.umpteenthdev.revolutsample.rates.di.DaggerRatesComponent
import com.umpteenthdev.revolutsample.rates.di.RatesComponent
import com.umpteenthdev.revolutsample.rates.entity.RateEntity
import com.umpteenthdev.revolutsample.rates.usecases.RatesEmitter
import com.umpteenthdev.revolutsample.rates.usecases.RatesListInitializer
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class RatesViewModel : ViewModel(), CoroutineScope {

    @Inject lateinit var initializer: RatesListInitializer
    @Inject lateinit var ratesEmitter: RatesEmitter
    @Inject lateinit var ratesCache: RatesCache
    @Inject lateinit var exceptionMapper: ExceptionMapper

    internal val component: RatesComponent = DaggerRatesComponent.factory().create(AppDependencies.instance).also {
        it.inject(this)
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO
    private val taskMutex: Mutex = Mutex()

    val uiStateBus: MutableLiveData<UiState> = MutableLiveData()
    private val emitterCallback: RatesEmitter.Callback by lazy { initRatesEmitterCallback() }
    private var shouldShowEmptyValues = false
    private var baseSymbol: String? = null
    private var baseAmount: Double = 1.0

    @Volatile private var order: List<String> = LinkedList()
    @Volatile private var isInitialized = false
    @Volatile private var currencyNames: Map<String, String> = mapOf()
    @Volatile private var currencyIconUrls: Map<String, String> = mapOf()
    @Volatile private var lastRates: List<RateUiModel> = listOf()

    //<editor-fold desc="Lifecycle">
    fun onConnect() {
        log("RatesViewModel onConnect")
        reload()
    }

    fun onDisconnect() {
        log("RatesViewModel onDisconnect")
        cache()
        ratesEmitter.remove(emitterCallback)
    }

    override fun onCleared() {
        log("RatesViewModel onClear")
        coroutineContext.cancelChildren()
    }
    //</editor-fold>

    fun onItemClicked(item: RateUiModel) {
        log("RatesViewModel onItemClick(${item.symbol} : ${item.amount})")
        baseSymbol = item.symbol
        addToOrder(item.symbol)

        val currentRates = lastRates.toMutableList()
        currentRates.remove(item)
        currentRates.add(0, item)
        lastRates = currentRates

        if (item.amount != null) {
            baseAmount = item.amount
            uiStateBus.value = UiState.Content(lastRates, moveToTop = true)
        } else {
            val rates = uiStateBus.value as? UiState.Content
            rates?.items?.toMutableList()?.let { newRates ->
                newRates.remove(item)
                newRates.add(0, item)
                uiStateBus.value = UiState.Content(newRates, moveToTop = true)
            }
        }

        ratesEmitter.setBase(baseSymbol)
    }

    fun onAmountChanged(item: RateUiModel, amount: Double?) {
        if (item.symbol != baseSymbol) return

        log("RatesViewModel onAmountChanged: $amount")

        when (amount) {
            null -> showLastRatesWithoutAmount()
            else -> changeBaseAmount(amount)
        }
    }

    fun onRetryLoading() {
        reload()
    }

    private fun reload() {
        log("RatesViewModel reloading")
        uiStateBus.value = UiState.Loading

        if (!isInitialized) initialize()
        launchTask {
            if (isInitialized) {
                ratesEmitter.observe(emitterCallback)
            } else {
                uiStateBus.postValue(UiState.ConnectionError)
            }
        }
    }

    private fun initRatesEmitterCallback(): RatesEmitter.Callback = object : RatesEmitter.Callback {

        override fun onNewRates(rates: RateEntity) {
            this@RatesViewModel.onNewRates(rates)
        }

        override fun onError(t: Throwable) {
            this@RatesViewModel.onError(t)
        }
    }

    private fun initialize() {
        log("RatesViewModel initialization")
        launchTask {
            isInitialized = when (val result = initializer.initialize()) {
                is RatesListInitializer.Result.Success -> {
                    processInitializationResult(result)
                    true
                }
                is RatesListInitializer.Result.Error -> {
                    onError(result.t)
                    false
                }
            }
        }
    }

    private fun processInitializationResult(result: RatesListInitializer.Result.Success) {
        currencyNames = result.currencyNames
        currencyIconUrls = result.currencyIconUrls
        if (result.cachedBaseAmount != null) {
            baseAmount = result.cachedBaseAmount
        }
        if (!result.cachedOrder.isNullOrEmpty()) {
            order = result.cachedOrder
            ratesEmitter.setBase(result.cachedOrder.first())
        }
    }

    private fun showLastRatesWithoutAmount() {
        shouldShowEmptyValues = true
        uiStateBus.value = UiState.Content(
            items = lastRates.map { it.copy(amount = null) },
            moveToTop = false
        )
    }

    private fun changeBaseAmount(amount: Double) {
        shouldShowEmptyValues = false
        val baseAmount = baseAmount
        this.baseAmount = amount

        val multiplier = amount / baseAmount
        lastRates = lastRates.map { it.copy(amount = it.amount!! * multiplier) }
        uiStateBus.value = UiState.Content(lastRates, moveToTop = false)
    }

    private fun onNewRates(rateEntity: RateEntity) = launchTask {
        log("RatesViewModel onNewRates")
        val result: LinkedList<RateUiModel> = LinkedList()

        if (baseSymbol == null) {
            baseSymbol = rateEntity.baseCurrencySymbol
            addToOrder(rateEntity.baseCurrencySymbol)
        }

        val baseSymbol = baseSymbol!!
        val baseAmount = baseAmount
        val newRates = rateEntity.rates.toMutableMap()

        val multiplier = when {
            baseSymbol != rateEntity.baseCurrencySymbol -> {
                val currentBaseRate = newRates[baseSymbol] ?: return@launchTask
                baseAmount / currentBaseRate
            }
            else -> baseAmount
        }

        result.add(getRateUiModel(rateEntity.baseCurrencySymbol, baseAmount))

        for (symbol in order) {
            newRates.remove(symbol)?.let { amount ->
                result.add(getRateUiModel(symbol, amount * multiplier))
            }
        }

        newRates.toSortedMap().forEach { (symbol, amount) ->
            result.add(getRateUiModel(symbol, amount * multiplier))
        }

        lastRates = result
        if (!shouldShowEmptyValues) uiStateBus.postValue(UiState.Content(result, moveToTop = false))
    }

    private fun getRateUiModel(symbol: String, amount: Double): RateUiModel {
        return RateUiModel(
            symbol = symbol,
            fullName = currencyNames[symbol] ?: "",
            iconUrl = currencyIconUrls[symbol] ?: "",
            amount = amount
        )
    }

    private fun addToOrder(symbol: String) {
        log("RatesViewModel addToOrder: $symbol")
        order = order.toMutableList().apply {
            remove(symbol)
            add(0, symbol)
        }
    }

    private fun cache() {
        ratesCache.baseAmount = baseAmount
        ratesCache.order = LinkedList(order)
    }

    private fun launchTask(block: suspend CoroutineScope.() -> Unit) = launch(start = CoroutineStart.UNDISPATCHED) {
        taskMutex.withLock {
            yield()
            block()
        }
    }

    private fun onError(t: Throwable) {
        log("RatesViewModel onError")
        ratesEmitter.remove(emitterCallback)

        when (val exception = exceptionMapper.mapException(t)) {
            is RevException.ConnectionError -> uiStateBus.postValue(UiState.ConnectionError)
            is RevException.UnknownException -> {
                log(exception.cause)
                throw exception.cause
            }
        }.requireAllBranches()
    }
}
