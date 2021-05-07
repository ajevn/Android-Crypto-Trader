package no.ajevn.cryptotrader.data.database.entities

import androidx.room.*

@Entity(tableName = "user_transactions",
        foreignKeys = [ForeignKey(
                entity = User::class,
                parentColumns = arrayOf("user_id"),
                childColumns = arrayOf("user_id"),
                onDelete = ForeignKey.CASCADE
        )])
data class UserTransactions (
        @ColumnInfo(name = "user_id")
        val userId: Long,
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "transaction_id")
        val transactionId: Long,
        @ColumnInfo(name = "transaction_type")
        val transactionType: String,
        @ColumnInfo(name = "transaction_currency_id")
        val currencyId: String,
        @ColumnInfo(name = "transaction_currency_symbol")
        val currencySymbol: String,
        @ColumnInfo(name = "transaction_currency_amount")
        val currencyAmount: String,
)



