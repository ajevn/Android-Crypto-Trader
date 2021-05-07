package no.ajevn.cryptotrader.API

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import no.ajevn.cryptotrader.data.JSON.JSONStringToBigDecimalAdapter
import no.ajevn.cryptotrader.service.CoinCapService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

//Initializing instances of retrofit and moshi
private val moshi = Moshi.Builder()
    .add(JSONStringToBigDecimalAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl("https://api.coincap.io/v2/")
    .build()

object API {
    val coinCapService: CoinCapService by lazy{
        retrofit.create(CoinCapService::class.java)
    }
}
