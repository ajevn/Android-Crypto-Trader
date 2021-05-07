package no.ajevn.cryptotrader.data.viewModels

import android.content.Context
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

class CurrencyTransactionViewModel : ViewModel() {

    private lateinit var userDao: IUserDAO
    private val coinCapService = API.coinCapService

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

    private fun getUserBalance(userId: Long, currencyParameter: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error ${exception.message!!}")
        }) {
            val userCurrencyBalance = userDao.getUserCurrencyById(userId, currencyParameter)
            if(userCurrencyBalance != null){
                _userCurrencyBalance.postValue(userCurrencyBalance)
            }
        }
    }

    //Updates currency price every 10 seconds when user is in Transaction activity with corresponding fragments for buy/sell
    //TODO - Check whether it is necessary to use this
    @InternalCoroutinesApi
    fun getCurrencyByIdAutoUpdate(currencyId: String) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error ${exception.message!!}")
        }) {
            while (NonCancellable.isActive) {
                val currency = coinCapService.getCurrencyById(currencyId)
                if(currency != null){
                    _singleCurrency.postValue(currency.data)
                }
                delay(5000)
            }
        }
    }
}