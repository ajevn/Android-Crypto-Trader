package no.ajevn.cryptotrader.service

import no.ajevn.cryptotrader.data.AllCurrenciesJsonResponse
import no.ajevn.cryptotrader.data.SingleCurrencyJsonResponse
import retrofit2.http.GET
import retrofit2.http.Path

//BASE URL = "https://api.coincap.io/v2/"
interface CoinCapService {

    @GET("assets/")
    suspend fun getAllCurrencies(): AllCurrenciesJsonResponse

    @GET("assets/{id}")
    suspend fun getCurrencyById(@Path("id") id: String): SingleCurrencyJsonResponse
}
