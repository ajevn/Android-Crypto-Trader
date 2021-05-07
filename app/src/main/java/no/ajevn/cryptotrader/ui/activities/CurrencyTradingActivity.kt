package no.ajevn.cryptotrader.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.CurrencyTransactionViewModel
import no.ajevn.cryptotrader.databinding.ActivityCurrencyTransactionBinding
import no.ajevn.cryptotrader.ui.fragments.BuyCurrencyFragment
import no.ajevn.cryptotrader.ui.fragments.FragmentClickCommunicator
import no.ajevn.cryptotrader.ui.fragments.SellCurrencyFragment
import no.ajevn.cryptotrader.ui.fragments.UserBuySellFragment

class CurrencyTradingActivity : AppCompatActivity(), FragmentClickCommunicator {

    private val viewModel: CurrencyTransactionViewModel by viewModels()
    lateinit var binding: ActivityCurrencyTransactionBinding
    lateinit var currencyId: String

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currencyId = intent.getStringExtra("currencyId")!!
        initObservers(currencyId)
        initFragment()
    }

    @InternalCoroutinesApi
    override fun onResume() {
        super.onResume()
        //Reloading currencies - On observed changes in data recycler view adapter is updated
        viewModel.getCurrencyByIdAutoUpdate(currencyId)
    }

    private fun initFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.transaction_fragment_container, UserBuySellFragment.newInstance(currencyId))
            .commit()
    }

    @InternalCoroutinesApi
    private fun initObservers(currencyId: String) {
        viewModel.singleCurrency.observe(this, Observer { currency ->
            Glide.with(this)
                .load("https://static.coincap.io/assets/icons/" + currency.symbol.toLowerCase() + "@2x.png")
                .into(binding.currencyTransactionItemIcon)
            binding.currencyTransactionItemTextId.text = currency.id.capitalize()
            binding.currencyTransactionItemTextSymbol.text = currency.symbol
            binding.currencyTransactionItemTextPrice.text = "$" + currency.priceUsd.toString()
        })
        viewModel.error.observe(this, Observer { error ->
            if (!error.isNullOrEmpty()) {
                Snackbar.make(
                    binding.root,
                    "Error retrieving data, please check network connection.",
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction("Retry") { viewModel.getCurrencyByIdAutoUpdate(currencyId) }
                    .show()
            }
        })
    }

    //Implementing functions from interface registering clicks in fragment
    override fun handleFragmentOnClick(action: String) {
        if (action === "Buy") {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.transaction_fragment_container,
                    BuyCurrencyFragment.newInstance(currencyId)
                )
                .addToBackStack("transactionFragmentBuy")
                .commit()
        } else if(action === "Sell") {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.transaction_fragment_container,
                    SellCurrencyFragment.newInstance(currencyId)
                )
                .addToBackStack("transactionFragmentSell")
                .commit()
        }
    }
}