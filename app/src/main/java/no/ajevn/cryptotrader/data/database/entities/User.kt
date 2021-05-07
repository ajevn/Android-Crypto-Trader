package no.ajevn.cryptotrader.data.database.entities

import androidx.room.*

@Entity(tableName = "user")
data class User(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "user_id")
        val userId: Long = 0,
        @ColumnInfo(name = "user_cash_on_hand")
        val userCashOnHand: String,
)

