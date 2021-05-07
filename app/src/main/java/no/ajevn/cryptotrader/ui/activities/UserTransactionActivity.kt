package no.ajevn.cryptotrader.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import no.ajevn.cryptotrader.data.viewModels.UserTransactionViewModel
import no.ajevn.cryptotrader.databinding.ActivityUserTransactionBinding
import no.ajevn.cryptotrader.ui.adapters.UserTransactionListAdapter

class UserTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserTransactionBinding
    private val viewModel: UserTransactionViewModel by viewModels()
    private var userId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = this.getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        userId = preferences.getLong("current_user_id", 0)

        viewModel.init(this, userId!!)
        initObservers()
    }

    private fun initObservers() {
        //RecyclerView
        val recyclerView = binding.rwUserTransactionRecyclerView;
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allTransactions.observe(this, { transactionList ->
            recyclerView.adapter = UserTransactionListAdapter(transactionList)
        })
        viewModel.error.observe(this, { error ->
            if(error.isNotEmpty()){
                Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") { viewModel.init(this, userId!!) }
                    .show()
            }
        })
    }
}