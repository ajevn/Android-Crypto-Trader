package no.ajevn.cryptotrader.data.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import no.ajevn.cryptotrader.API.API
import no.ajevn.cryptotrader.data.CurrencyItem
import no.ajevn.cryptotrader.data.database.DAO.IUserDAO
import no.ajevn.cryptotrader.data.database.Database
import java.math.BigDecimal
import java.math.RoundingMode

class UserBuyCurrencyViewModel : ViewModel() {

    private val coinCapService = API.coinCapService
    private lateinit var userDao: IUserDAO

    val singleCurrency: LiveData<CurrencyItem> get() = _singleCurrency
    private val _singleCurrency = MutableLiveData<CurrencyItem>()

    val userBalance: LiveData<BigDecimal> get() = _userBalance
    private val _userBalance = MutableLiveData<BigDecimal>()

    val error = MutableLiveData<String>()

    @InternalCoroutinesApi
    fun init(context: Context, userId: Long, currencyParameter: String?) {
        userDao = Database.getDatabase(context).getUserDao()
        getCurrencyByIdAutoUpdate(currencyParameter!!)
        getUserBalance(userId)
    }

    //Purchase function. Calls appropriate DAO functions -- Informs user if balance is insufficient for purchase
    fun purchaseCurrency(currencyId: String, dollarPurchaseAmount: BigDecimal, userId: Long){
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Purchase transaction error ${exception.message!!}")
            Log.d("Main","Purchase transaction error ${exception.message!!}")
        }) {
            val updatedCurrencyData = coinCapService.getCurrencyById(currencyId).data
            val userData = userDao.getUserByUserId(userId)
            if(userData.userCashOnHand.toBigDecimal().compareTo(dollarPurchaseAmount) == -1) {
                error.postValue("User balance insufficient. Current balance: $${userData.userCashOnHand}")
            } else {
                val currencyValue = dollarPurchaseAmount.divide(updatedCurrencyData.priceUsd, 6, RoundingMode.UP)
                userDao.purchaseCurrency(userId, currencyId, updatedCurrencyData.symbol, currencyValue, dollarPurchaseAmount)
                getUserBalance(userId)
            }
        }
    }

    //Sell function. Calls appropriate DAO functions -- Informs user if balance is insufficient for sale
    private fun getUserBalance(userId: Long) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error retrieving user information ${exception.message!!}")
            Log.d("Main","Purchase transaction error ${exception.message!!}")
        }) {
            val userBalance = userDao.getUserByUserId(userId).userCashOnHand.toBigDecimal()
            _userBalance.postValue(userBalance)
        }
    }

    fun getCurrencyById(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error retrieving currency ${exception.message!!}")
        }) {
            val currency = coinCapService.getCurrencyById(currencyId)
            _singleCurrency.postValue(currency.data)
        }
    }

    //Updates currency price every 10 seconds when user is in Transaction activity with corresponding fragments for buy/sell
    //TODO - Check whether it is necessary to use this
    @InternalCoroutinesApi
    fun getCurrencyByIdAutoUpdate(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error retrieving currency ${exception.message!!}")
        }) {
            while (NonCancellable.isActive) {
                val currency = coinCapService.getCurrencyById(currencyId)
                _singleCurrency.postValue(currency.data)
                delay(5000)
            }
        }
    }
}