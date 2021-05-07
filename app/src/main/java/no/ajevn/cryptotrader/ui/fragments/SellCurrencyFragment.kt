package no.ajevn.cryptotrader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.UserSellCurrencyViewModel
import no.ajevn.cryptotrader.databinding.FragmentSellCurrencyBinding
import java.math.BigDecimal
import java.math.RoundingMode


class SellCurrencyFragment : Fragment(R.layout.fragment_sell_currency) {

    private val viewModel: UserSellCurrencyViewModel by viewModels()
    private lateinit var binding: FragmentSellCurrencyBinding
    private var currencyParameter: String? = null
    private var userBalance: Long? = null
    private var userId: Long? = null
    private var currencyPrice: BigDecimal? = null
    private var currencyValue: BigDecimal? = null
    private var currencySellAmount: BigDecimal? = null

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSellCurrencyBinding.bind(view)

        //Extracting currencyId from bundle passed to fragment - Allows fragment to use data in its own viewmodel
        arguments?.let {
            currencyParameter = it.getString("currency")
            if (currencyParameter == null) {
                showError()
            }
        }

        //Initializing functions - viewModel initialized with parameters. Not best practice, should be handled in ViewModelFactory with constructor
        val preferences = requireActivity().getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        userId = preferences.getLong("current_user_id", 0)
        viewModel.init(requireContext(), userId!!, currencyParameter)
        initViewListeners()
        initObservers(currencyParameter!!)

        //Set button alpha to 0 if user dollar is <= 0$
        binding.fragmentSellCurrencyButtonSell.isEnabled = false;
        binding.fragmentSellCurrencyButtonSell.alpha = 0.5F;

        //sell currency
        binding.fragmentSellCurrencyButtonSell.setOnClickListener {
            val preferences = requireActivity().getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
            val userId = preferences.getLong("current_user_id", 0)
            if(!userId.equals(0)){
                if(!currencyParameter.isNullOrEmpty() || !currencySellAmount.toString().isNullOrEmpty()) {
                    viewModel.sellCurrency(currencyParameter!!, currencySellAmount!!, userId)
                }
                view.hideKeyboard()
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun initObservers(currencyId: String) {
        viewModel.singleCurrency.observe(viewLifecycleOwner) { currency ->
            currencyPrice = currency.priceUsd
        }
        viewModel.userCurrencyBalance.observe(viewLifecycleOwner) { retrievedUserCurrency ->
                val userCurrencyBalance = retrievedUserCurrency.currencyAmount
                binding.fragmentSellCurrencySymbolTw.text = retrievedUserCurrency.currencySymbol
                binding.fragmentSellCurrencyBalanceTw.text = userCurrencyBalance
                binding.fragmentSellCurrencyButtonSell.isEnabled = true;
                binding.fragmentSellCurrencyButtonSell.alpha = 1F;
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Snackbar.make(
                        binding.root,
                        error,
                        Snackbar.LENGTH_LONG
                )
                        .setAction("Retry") { viewModel.getCurrencyById(currencyId) }
                        .show()
            }
        }
    }

    private fun initViewListeners(){
        //Convert user input in dollars to value in chosen currency
        binding.fragmentSellCurrencyEditCurrencyAmount.doAfterTextChanged {
            if (binding.fragmentSellCurrencyEditCurrencyAmount.text.toString().isNotEmpty()) {
                //viewModel.getCurrencyById(currencyParameter!!)
                currencySellAmount = binding.fragmentSellCurrencyEditCurrencyAmount.text.toString().toBigDecimal()
                currencyValue = currencySellAmount!!.multiply(currencyPrice).setScale(6, RoundingMode.HALF_UP)
                binding.fragmentSellCurrencyTwUsdAmount.text = currencyValue.toString();
            } else {
                binding.fragmentSellCurrencyTwUsdAmount.text = "0"
            }
        }
    }

    private fun showError() {
        Log.d("Main", "Error passing currency to sell fragment")
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SellCurrencyFragment().apply {
                arguments = Bundle().apply {
                    putString("currency", param1)
                }
            }
    }
}