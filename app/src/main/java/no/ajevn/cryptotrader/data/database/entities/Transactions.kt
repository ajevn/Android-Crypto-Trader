package no.ajevn.cryptotrader.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "transactions_table")
data class Transactions (
    @ColumnInfo(name = "userId")
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val currency: String,
    @ColumnInfo(name = "points")
    val amount: Long,
    @ColumnInfo(name = "date")
    val date: Date
)