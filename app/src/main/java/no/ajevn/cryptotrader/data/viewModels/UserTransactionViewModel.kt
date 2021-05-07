package no.ajevn.cryptotrader.data.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.ajevn.cryptotrader.data.database.DAO.IUserDAO
import no.ajevn.cryptotrader.data.database.Database
import no.ajevn.cryptotrader.data.database.entities.UserTransactions


class UserTransactionViewModel: ViewModel(){
    private lateinit var userDao: IUserDAO

    val allTransactions: LiveData<List<UserTransactions>> get() = _allTransactions
    private val _allTransactions = MutableLiveData<List<UserTransactions>>()

    val error = MutableLiveData<String>()

    fun init(context: Context, userId: Long) {
        userDao = Database.getDatabase(context).getUserDao()
        loadAllTransactions(userId)
    }

    private fun loadAllTransactions(userId: Long){
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            error.postValue("Error retrieving transactions")
            Log.d("Main", exception.message!!)
        }) {
            val allTransactions = userDao.getAllUserTransactions(userId)
            _allTransactions.postValue(allTransactions)
        }
    }
}