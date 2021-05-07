package no.ajevn.cryptotrader.data.database.entities

import androidx.room.*

@Entity(tableName = "user_currencies",
        foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("user_id"),
        childColumns = arrayOf("user_id"),
        onDelete = ForeignKey.CASCADE
)])
data class UserCurrencies (
        @ColumnInfo(name = "user_id")
        val userId: Long,
        @PrimaryKey(autoGenerate = false)
        @ColumnInfo(name = "currency_id")
        val currencyId: String,
        @ColumnInfo(name = "currency_symbol")
        val currencySymbol: String,
        @ColumnInfo(name = "currency_amount")
        val currencyAmount: String,
)




