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
import no.ajevn.cryptotrader.data.database.entities.UserCurrencies
import java.math.BigDecimal

class UserSellCurrencyViewModel : ViewModel() {

    private val coinCapService = API.coinCapService
    private lateinit var userDao: IUserDAO

    val singleCurrency: LiveData<CurrencyItem> get() = _singleCurrency
    private val _singleCurrency = MutableLiveData<CurrencyItem>()

    val userCurrencyBalance: LiveData<UserCurrencies> get() = _userCurrencyBalance
    private val _userCurrencyBalance = MutableLiveData<UserCurrencies>()

    val error = MutableLiveData<String>()

    @InternalCoroutinesApi
    fun init(context: Context, userId: Long, currencyParameter: String?) {
        userDao = Database.getDatabase(context).getUserDao()
        getCurrencyByIdAutoUpdate(currencyParameter!!)
        getUserBalance(userId, currencyParameter!!)
    }

    fun sellCurrency(currencyId: String, currencySellAmount: BigDecimal, userId: Long) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Selling transaction error ${exception.message!!}")
            Log.d("Main", "Selling transaction error ${exception.message!!}")
        }) {
            val updatedCurrencyData = coinCapService.getCurrencyById(currencyId).data
            val userCurrencyData = userDao.getUserCurrencyById(userId, currencyId)
            if (userCurrencyData === null || userCurrencyData.currencyAmount.toBigDecimal().compareTo(currencySellAmount) == -1) {
                error.postValue("Currency balance insufficient")
            } else {
                val currencyValueUsd = currencySellAmount.multiply(updatedCurrencyData.priceUsd)
                userDao.sellCurrency(userId, currencyId, updatedCurrencyData.symbol, currencyValueUsd, currencySellAmount)
                getUserBalance(userId, currencyId)
            }
        }
    }

    private fun getUserBalance(userId: Long, currencyParameter: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            Log.d("Main","No currencies of this type for this user.")
        }) {
            val userCurrencyBalance = userDao.getUserCurrencyById(userId, currencyParameter)
            if(userCurrencyBalance != null){
                _userCurrencyBalance.postValue(userCurrencyBalance)
            }
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