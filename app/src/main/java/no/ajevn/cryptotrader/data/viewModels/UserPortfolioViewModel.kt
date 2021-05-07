package no.ajevn.cryptotrader.data.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.ajevn.cryptotrader.API.API.coinCapService
import no.ajevn.cryptotrader.data.database.DAO.IUserDAO
import no.ajevn.cryptotrader.data.database.Database
import no.ajevn.cryptotrader.data.database.entities.UserCurrencies
import java.math.BigDecimal
import java.math.RoundingMode

//Data class such that price can be retrieved from DAO and submitted along with standard UserCurrencies object
data class UserCurrenciesWithPrice (
        val userId: Long,
        val currencyId: String,
        val currencySymbol: String,
        val currencyAmount: String,
        val updatedCurrencyPrice: BigDecimal
)

class UserPortfolioViewModel : ViewModel() {

    private lateinit var userDao: IUserDAO

    val allUserCurrencies: LiveData<List<UserCurrencies>> get() = _allUserCurrencies
    private val _allUserCurrencies = MutableLiveData<List<UserCurrencies>>()

    val allUserCurrenciesWithPrice: LiveData<ArrayList<UserCurrenciesWithPrice>> get() = _allUserCurrenciesWithPrice
    private val _allUserCurrenciesWithPrice = MutableLiveData<ArrayList<UserCurrenciesWithPrice>>()

    val updatedUserTotalBalance: LiveData<BigDecimal> get() = _updatedUserTotalBalance
    private val _updatedUserTotalBalance = MutableLiveData<BigDecimal>()

    val updatedUserBalance: LiveData<BigDecimal> get() = _updatedUserBalance
    private val _updatedUserBalance = MutableLiveData<BigDecimal>()

    val updatedUserChange: LiveData<String> get() = _updatedUserChange
    private val _updatedUserChange = MutableLiveData<String>()

    val error = MutableLiveData<String>()

    fun init(context: Context, userId: Long) {
        userDao = Database.getDatabase(context).getUserDao()
        loadAllUserCurrenciesAndUserBalance(userId)
        loadAllUserCurrenciesWithPrice(userId)
    }

    private fun loadAllUserCurrenciesWithPrice(userId: Long) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error retrieving user currencies")
            Log.d("Main", exception.message!!)
        }) {
            val allUserCurrencies = userDao.getAllUserCurrencies(userId)
            val currenciesList = ArrayList<UserCurrenciesWithPrice>()
            //_allUserCurrenciesWithPrice.postValue(allUserCurrencies)

            for(currency in allUserCurrencies){
                val currencyData = coinCapService.getCurrencyById(currency.currencyId)
                val currencyObject = UserCurrenciesWithPrice(userId, currency.currencyId, currency.currencySymbol, currency.currencyAmount, currencyData.data.priceUsd)

                currenciesList.add(currencyObject)
            }
            _allUserCurrenciesWithPrice.postValue(currenciesList)
        }
    }

    fun loadAllUserCurrenciesAndUserBalance(userId: Long){
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error retrieving user currencies")
            Log.d("Main", exception.message!!)
        }) {

            val user = userDao.getUserByUserId(userId)

            if(user != null) {
                val allUserCurrencies = userDao.getAllUserCurrencies(userId)
                _allUserCurrencies.postValue(allUserCurrencies)

                var userBalance = userDao.getUserByUserId(userId).userCashOnHand.toBigDecimal()
                    _updatedUserBalance.postValue(userBalance)

                    allUserCurrencies.forEach { currencyListItem ->
                        val updatedCurrency = coinCapService.getCurrencyById(currencyListItem.currencyId).data
                        val userCurrencyValueUsd = currencyListItem.currencyAmount.toBigDecimal().multiply(updatedCurrency.priceUsd).setScale(2, RoundingMode.HALF_UP)
                        userCurrencyValueUsd.setScale(2, RoundingMode.HALF_UP)

                        val updatedUserBalance = userBalance.add(userCurrencyValueUsd)
                        userBalance = updatedUserBalance
                    }

                    val initialValue = BigDecimal(10000)
                    val updatedUserChange: BigDecimal

                    if(initialValue != null){
                        if(initialValue > userBalance){
                            updatedUserChange = initialValue.subtract(userBalance).setScale(1, RoundingMode.HALF_UP)
                            _updatedUserChange.postValue("- $updatedUserChange")
                        } else if(initialValue <= userBalance){
                            updatedUserChange = userBalance.subtract(initialValue).setScale(1, RoundingMode.HALF_UP)
                            _updatedUserChange.postValue("+ $updatedUserChange")
                        }
                    }
                    if(userBalance != null){
                        _updatedUserTotalBalance.postValue(userBalance)
                    }
            }
        }
    }
}

