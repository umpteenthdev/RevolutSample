package com.umpteenthdev.revolutsample.rates.adapters.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {

    @GET("latest")
    suspend fun getRates(@Query("base") base: String?): Rates

    @GET("https://raw.githubusercontent.com/umpteenthdev/RevolutSample/master/currencies_names_mock_api.json")
    suspend fun getCurrenciesNames(): Map<String, String>

    data class Rates(
        @SerializedName("base") val base: String,
        @SerializedName("date") val date: String,
        @SerializedName("rates") val rates: Map<String, Double>
    )
}
