package no.ajevn.cryptotrader.data.JSON

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import no.ajevn.cryptotrader.data.StringToBigDecimal
import java.math.BigDecimal

//Custom Moshi adapter casting String values to Int. This is because API returns String data for numerical values.
internal class JSONStringToBigDecimalAdapter {
    @FromJson
    @StringToBigDecimal
    fun fromJson(value: String): BigDecimal {
        return value.toBigDecimal()
    }

    @ToJson
    fun toJson(@StringToBigDecimal value: BigDecimal): String {
        return value.toString()
    }
}