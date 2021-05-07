package no.ajevn.cryptotrader.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.content.edit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.MainActivityViewModel
import no.ajevn.cryptotrader.databinding.ActivityMainBinding
import no.ajevn.cryptotrader.ui.adapters.CurrencyItemListAdapter
import no.ajevn.cryptotrader.ui.fragments.UserScoreFragment

class MainActivity : AppCompatActivity(), CurrencyItemListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val userPointsFragment = UserScoreFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        //Changing from Splash theme on startup back to default App Theme
        setTheme(R.style.Theme_CryptoTrader)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObservers()
        viewModel.init(this)
        validateFirstTimeReward()
    }


    //Checks if user already redeemed reward, else writes to database and updates DAO
    private fun validateFirstTimeReward(){
        val sharedPreferences = getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        val firstTimeReward = sharedPreferences.getBoolean("first_time_reward_redeemed", false)
        if(!firstTimeReward) {
            Log.d("Main", "First time reward redeemed = $firstTimeReward, adding points and creating user")
            viewModel.createUser()
            sharedPreferences.edit() {
                putBoolean("first_time_reward_redeemed", true)
                apply()
            }
        } else{
            Log.d("Main", "First time reward redeemed = $firstTimeReward")
        }
        initFragment()
    }

    private fun initObservers() {
        //RecyclerView
        val recyclerView = binding.rwCurrencyListMain;
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.allCurrencies.observe(this,  { list ->
            recyclerView.adapter = CurrencyItemListAdapter(list, this)
        })
        viewModel.userIdLiveData.observe(this, { userId ->
            val sharedPreferences = getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
            sharedPreferences.edit() {
                putLong("current_user_id", userId)
                apply()
            }

        })
        viewModel.error.observe(this, { error ->
            if(error){
                Snackbar.make(binding.root, "Error retrieving data, please check network connection.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") { viewModel.loadAllCurrencies() }
                    .show()
            }
        })
    }

    private fun initFragment(){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_user_points, userPointsFragment)
                    .detach(userPointsFragment)
                    .attach(userPointsFragment)
            commit()
        }

        binding.fragmentUserPoints.setOnClickListener {
            val intent = Intent(this, UserPortfolioActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAllCurrencies()
        initFragment()
    }

    override fun onItemClick(currency: String) {
        val intent = Intent(this, CurrencyTradingActivity::class.java)
        intent.putExtra("currencyId", currency)
        startActivity(intent)
    }
}