package no.ajevn.cryptotrader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.UserBuyCurrencyViewModel
import no.ajevn.cryptotrader.databinding.FragmentBuyCurrencyBinding
import java.math.BigDecimal
import java.math.RoundingMode


class BuyCurrencyFragment : Fragment(R.layout.fragment_buy_currency) {

    private val viewModel: UserBuyCurrencyViewModel by viewModels()

    private lateinit var binding: FragmentBuyCurrencyBinding
    private var currencyParameter: String? = null
    private var userBalance: BigDecimal? = null
    private var userId: Long? = null
    private var currencyPrice: BigDecimal? = null
    private var currencyValue: BigDecimal? = null
    private var dollarPurchaseAmount: BigDecimal? = null

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBuyCurrencyBinding.bind(view)

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
        binding.fragmentBuyCurrencyButtonBuy.isEnabled = false;
        binding.fragmentBuyCurrencyButtonBuy.alpha = 0.5F;

        //buy currency
        binding.fragmentBuyCurrencyButtonBuy.setOnClickListener {
            val preferences = requireActivity().getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
            val userId = preferences.getLong("current_user_id", 0)
            if(!userId.equals(0)){
                if(!currencyParameter.isNullOrEmpty() || !dollarPurchaseAmount.toString().isNullOrEmpty())
                viewModel.purchaseCurrency(currencyParameter!!, dollarPurchaseAmount!!, userId)
                view.hideKeyboard()
                binding.fragmentBuyCurrencyEditUsd.text = null
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
        viewModel.userBalance.observe(viewLifecycleOwner) { retrievedUserBalance ->
            userBalance = retrievedUserBalance
            binding.fragmentBuyBalanceTw.text = "$${retrievedUserBalance.toString()} USD"
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
        binding.fragmentBuyCurrencyEditUsd.doAfterTextChanged {
            if (binding.fragmentBuyCurrencyEditUsd.text.toString().isNotEmpty()) {
                dollarPurchaseAmount = binding.fragmentBuyCurrencyEditUsd.text.toString().toBigDecimal()
                currencyValue = dollarPurchaseAmount!!.divide(currencyPrice, 6, RoundingMode.UP)
                binding.fragmentBuyCurrencyTwCurrencyAmount.text = currencyValue.toString();
                binding.fragmentBuyCurrencyButtonBuy.isEnabled = true;
                binding.fragmentBuyCurrencyButtonBuy.alpha = 1F;
            } else {
                binding.fragmentBuyCurrencyTwCurrencyAmount.text = "0"
                binding.fragmentBuyCurrencyButtonBuy.isEnabled = false;
                binding.fragmentBuyCurrencyButtonBuy.alpha = 0.5F;
            }
        }
    }

    private fun showError() {
        Snackbar.make(
                binding.root,
                "Error passing currency to fragment",
                Snackbar.LENGTH_LONG
        )
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            BuyCurrencyFragment().apply {
                arguments = Bundle().apply {
                    putString("currency", param1)
                }
            }
    }


}