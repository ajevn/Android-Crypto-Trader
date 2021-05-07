package no.ajevn.cryptotrader.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import no.ajevn.cryptotrader.data.database.DAO.IUserDAO
import no.ajevn.cryptotrader.data.database.entities.User
import no.ajevn.cryptotrader.data.database.entities.UserCurrencies
import no.ajevn.cryptotrader.data.database.entities.UserTransactions
import no.ajevn.cryptotrader.data.database.entities.converters.DateToDoubleConverter

const val DATABASE_NAME: String = "cryptotrader_database"
@androidx.room.Database(entities = [
    User::class,
    UserCurrencies::class,
    UserTransactions::class
    ],
    version = 1)
@TypeConverters(DateToDoubleConverter::class)
abstract class Database : RoomDatabase() {
    abstract fun getUserDao() : IUserDAO

    companion object {
        var db : Database? = null

        fun getDatabase(context: Context) : Database{
            val newDb = db?: Room.databaseBuilder(context, Database::class.java, DATABASE_NAME).build()
            return newDb.also {
                db = it
            }
        }
    }
}