package no.ajevn.cryptotrader.data.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.ajevn.cryptotrader.API.API
import no.ajevn.cryptotrader.data.CurrencyItem
import no.ajevn.cryptotrader.data.database.DAO.IUserDAO
import no.ajevn.cryptotrader.data.database.Database
import no.ajevn.cryptotrader.data.database.entities.User
import no.ajevn.cryptotrader.data.database.entities.UserTransactions


class MainActivityViewModel: ViewModel(){

    private val coinCapService = API.coinCapService
    private lateinit var userDao: IUserDAO
    private var userId: Long? = null

    val allCurrencies: LiveData<List<CurrencyItem>> get() = _allCurrencies
    private val _allCurrencies = MutableLiveData<List<CurrencyItem>>()

    val userIdLiveData: LiveData<Long> get() = _userId
    private val _userId = MutableLiveData<Long>()

    val error = MutableLiveData<Boolean>()

    fun init(context: Context) {
        loadAllCurrencies()
        userDao = Database.getDatabase(context).getUserDao()
    }

    fun createUser() {
        viewModelScope.launch {
            try {
                val createdUserId = userDao.insertUser(User(0, "10000"))
                userDao.insertTransaction(UserTransactions(createdUserId, 0, "Reward", "Installation Reward", "", "10000"))
                _userId.value = createdUserId
                Log.d("Main", "Created user with ID ${createdUserId}")
            } catch (e: Exception) {
                e.fillInStackTrace()
            }
        }
    }

    //Loads all currencies and makes it observable as Live data for Activity to use in RecyclerView Adapter. Updates to prices will result in update in recyclerView
    fun loadAllCurrencies(){
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue(true)
            Log.d("Main", exception.message!!)
        }) {
            val allCurrency = coinCapService.getAllCurrencies()
            _allCurrencies.postValue(allCurrency.data)
        }
    }
}