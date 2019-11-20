package com.umpteenthdev.revolutsample.rates.di

import android.content.Context
import com.umpteenthdev.revolutsample.rates.adapters.api.RatesApi
import com.umpteenthdev.revolutsample.rates.adapters.cache.RatesCache
import com.umpteenthdev.revolutsample.rates.adapters.flags.FlagImageUrlResolver
import com.umpteenthdev.revolutsample.rates.adapters.gateways.GithubCurrencyNamesGatewayImpl
import com.umpteenthdev.revolutsample.rates.adapters.gateways.RatesGatewayImpl
import com.umpteenthdev.revolutsample.rates.adapters.imageloading.ImageLoader
import com.umpteenthdev.revolutsample.rates.adapters.timing.Metronome
import com.umpteenthdev.revolutsample.rates.entity.RatesConfig
import com.umpteenthdev.revolutsample.rates.outer.android.RatesAdapter
import com.umpteenthdev.revolutsample.rates.outer.android.RatesAdapterFactory
import com.umpteenthdev.revolutsample.rates.outer.flags.GithubFlagImageUrlResolver
import com.umpteenthdev.revolutsample.rates.outer.glide.GlideImageLoader
import com.umpteenthdev.revolutsample.rates.outer.paper.PaperRatesCache
import com.umpteenthdev.revolutsample.rates.outer.timing.MetronomeTimerImpl
import com.umpteenthdev.revolutsample.rates.usecases.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
internal abstract class RatesModule {

    @Binds
    @RatesScope
    abstract fun metronome(impl: MetronomeTimerImpl): Metronome

    @Binds
    @RatesScope
    abstract fun imageLoader(impl: GlideImageLoader): ImageLoader

    @Binds
    @RatesScope
    abstract fun imageUrlResolver(impl: GithubFlagImageUrlResolver): FlagImageUrlResolver

    @Binds
    @RatesScope
    abstract fun ratesGateway(impl: RatesGatewayImpl): RatesGateway

    @Binds
    @RatesScope
    abstract fun ratesEmitter(impl: RatesEmitterImpl): RatesEmitter

    @Binds
    @RatesScope
    abstract fun currencyNamesGateway(impl: GithubCurrencyNamesGatewayImpl): CurrencyNamesGateway

    @Binds
    @RatesScope
    abstract fun ratesListInitializer(impl: RatesListInitializerImpl): RatesListInitializer

    @Binds
    @RatesScope
    abstract fun ratesCache(impl: PaperRatesCache): RatesCache

    @Module
    companion object {

        @Provides
        @JvmStatic
        @RatesScope
        fun ratesApi(client: OkHttpClient): RatesApi {
            return Retrofit.Builder()
                .client(client)
                .baseUrl(RatesConfig.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RatesApi::class.java)
        }

        @Provides
        @JvmStatic
        @RatesScope
        fun ratesAdapterFactory(imageLoader: ImageLoader, appContext: Context): RatesAdapterFactory {
            return object : RatesAdapterFactory {
                override fun create(callback: RatesAdapter.Callback): RatesAdapter {
                    return RatesAdapter(
                        imageLoader = imageLoader,
                        appContext = appContext,
                        callback = callback
                    )
                }
            }
        }
    }
}
