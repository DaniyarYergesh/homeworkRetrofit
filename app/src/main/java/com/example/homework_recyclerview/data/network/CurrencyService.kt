package com.example.homework_recyclerview.data.network

import com.example.homework_recyclerview.domain.models.CurrencyRates
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CurrencyService {

    @Headers("apikey: LLQm0urscE2G2AZxhJ4Y9EyE70TrPqDE")
    @GET("latest")
    suspend fun getCurrencyRates(
        @Query("base") base: String = "KZT"
    ):CurrencyRates

}