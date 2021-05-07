package no.ajevn.cryptotrader.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.UserPortfolioViewModel
import no.ajevn.cryptotrader.databinding.ActivityUserPortfolioBinding
import no.ajevn.cryptotrader.ui.adapters.UserPortfolioListAdapter
import no.ajevn.cryptotrader.ui.fragments.UserScoreFragment

class UserPortfolioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserPortfolioBinding
    private val viewModel: UserPortfolioViewModel by viewModels()
    private val userPointsFragment = UserScoreFragment()
    private var userId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPortfolioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preferences = this.getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        userId = preferences.getLong("current_user_id", 0)

        viewModel.init(this, userId!!)
        initFragment()
        initObservers()

        binding.activityUserPortfolioTransactionButton.setOnClickListener {
            val intent = Intent(this, UserTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initFragment(){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_user_points, userPointsFragment)
            commit()
        }
    }

    private fun initObservers() {
        //RecyclerView
        val recyclerView = binding.rwUserPortfolio;
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allUserCurrenciesWithPrice.observe(this, { currenciesList ->
            recyclerView.adapter = UserPortfolioListAdapter(currenciesList)
        })
        viewModel.updatedUserBalance.observe(this, { userBalance ->
            binding.activityUserPortfolioBalance.text = "${userBalance.toString()} USD"
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