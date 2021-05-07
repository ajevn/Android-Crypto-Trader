package no.ajevn.cryptotrader.data

import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonQualifier
import java.math.BigDecimal


@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class NumberConverter

@JsonClass(generateAdapter = true)
data class SingleCurrencyJsonResponse(
    //Parsing data as CurrencyItem since API endpoint sends nested JSON
    val data: CurrencyItem,
    val timestamp: String,
)

@JsonClass(generateAdapter = true)
data class AllCurrenciesJsonResponse(
    //Parsing data as CurrencyItem since API endpoint sends nested JSON
    val data: List<CurrencyItem>,
    val timestamp: String,
)


//Custom Moshi adapter converting String values to BigDecimal. This is because Exam API returns String data for numerical values.
@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class StringToBigDecimal

@JsonClass(generateAdapter = true)
data class CurrencyItem(
        val id: String,
        val rank: String?,
        val symbol: String,
        val name: String?,
        val supply: String?,
        val maxSupply: String?,
        val marketCapUsd: String?,
        val volumeUsd24Hr: String?,
        @StringToBigDecimal val priceUsd: BigDecimal,
        val changePercent24Hr: String,
        val vwap24Hr: String?,
        val explorer: String?,
)



