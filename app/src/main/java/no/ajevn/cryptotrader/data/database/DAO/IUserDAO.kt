package no.ajevn.cryptotrader.data.database.DAO

import android.util.Log
import androidx.room.*
import no.ajevn.cryptotrader.data.database.entities.User
import no.ajevn.cryptotrader.data.database.entities.UserCurrencies
import no.ajevn.cryptotrader.data.database.entities.UserTransactions
import java.math.BigDecimal
import java.math.RoundingMode

@Dao
interface IUserDAO {

    @Update
    suspend fun update(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Update
    suspend fun updateCurrency(userCurrencies: UserCurrencies)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(userTransactions: UserTransactions)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(userCurrencies: UserCurrencies)

    @Query("SELECT * FROM user where user_id = :userId")
    suspend fun getUserByUserId(userId: Long): User

    @Query("SELECT * FROM user_currencies where user_id = :userId and currency_id = :currencyId")
    suspend fun getUserCurrencyById(userId: Long, currencyId: String): UserCurrencies

    @Transaction
    suspend fun purchaseCurrency(userId: Long, currencyId: String, currencySymbol: String, currencyValue: BigDecimal, dollarPurchaseAmount: BigDecimal){
        val currencyLookupResult = getUserCurrencyById(userId, currencyId)
        if(currencyLookupResult === null){
            insertCurrency(UserCurrencies(userId, currencyId, currencySymbol, currencyValue.toString()))
            val currentUserBalance = getUserByUserId(userId)
            val updatedUserBalance = currentUserBalance.userCashOnHand.toBigDecimal().subtract(dollarPurchaseAmount)
            updateUser(User(userId, updatedUserBalance.toString()))

            insertTransaction(UserTransactions(userId,0, "Buy", currencyId, currencySymbol, currencyValue.toString()))
        } else {
            val currentCurrencyAmount = currencyLookupResult.currencyAmount
            val updatedCurrencyAmount = currentCurrencyAmount.toBigDecimal() + currencyValue
            Log.d("Main", "$currencyValue $dollarPurchaseAmount $updatedCurrencyAmount")

            updateCurrency(UserCurrencies(userId, currencyId, currencySymbol, updatedCurrencyAmount.toString()))
            val currentUserBalance = getUserByUserId(userId)
            val updatedUserBalance = currentUserBalance.userCashOnHand.toBigDecimal().subtract(dollarPurchaseAmount)
            updateUser(User(userId, updatedUserBalance.toString()))

            insertTransaction(UserTransactions(userId,0, "Buy", currencyId, currencySymbol, currencyValue.toString()))
        }
    }

    @Transaction
    suspend fun sellCurrency(userId: Long, currencyId: String, currencySymbol: String, currencyValueUsd: BigDecimal, currencySellAmount: BigDecimal){
        val currencyLookupResult = getUserCurrencyById(userId, currencyId)

        val currentCurrencyAmount = currencyLookupResult.currencyAmount.toBigDecimal()
        val updatedCurrencyAmount = currentCurrencyAmount.subtract(currencySellAmount).setScale(8, RoundingMode.HALF_UP)
        updateCurrency(UserCurrencies(userId, currencyId, currencySymbol, updatedCurrencyAmount.toString()))

        val currentUserBalance = getUserByUserId(userId)
        val updatedUserBalance = currentUserBalance.userCashOnHand.toBigDecimal().add(currencyValueUsd).setScale(2, RoundingMode.HALF_UP)
        updateUser(User(userId, updatedUserBalance.toString()))

        insertTransaction(UserTransactions(userId,0, "Sell", currencyId, currencySymbol, currencySellAmount.toString()))
    }

    @Query("SELECT * FROM user_transactions where user_id = :userId")
    suspend fun getAllUserTransactions(userId: Long): List<UserTransactions>

    @Query("SELECT * FROM user_currencies where user_id = :userId")
    suspend fun getAllUserCurrencies(userId: Long): List<UserCurrencies>
}