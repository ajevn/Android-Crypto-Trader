package no.ajevn.cryptotrader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.InternalCoroutinesApi
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.CurrencyTransactionViewModel
import no.ajevn.cryptotrader.databinding.FragmentUserBuySellBinding
import java.math.BigDecimal
import java.math.RoundingMode

class UserBuySellFragment : Fragment(R.layout.fragment_user_buy_sell) {

    private val viewModel: CurrencyTransactionViewModel by viewModels()
    private lateinit var binding: FragmentUserBuySellBinding
    private lateinit var communicator: FragmentClickCommunicator
    private var currencyParameter: String? = null
    private var userBalance: BigDecimal? = null
    private var currencyPrice: BigDecimal? = null


    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserBuySellBinding.bind(view)

        //Onclick in fragment
        communicator = activity as FragmentClickCommunicator

        //Extracting currencyId from bundle passed to fragment - Allows fragment to use data in its own viewmodel
        arguments?.let {
            currencyParameter = it.getString("currency")
            if (currencyParameter == null) {
                showError()
            }
        }

        val preferences = requireActivity().getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        val userId = preferences.getLong("current_user_id", 0)
        viewModel.init(requireContext(), userId, currencyParameter)
        initObservers()

        binding.fragmentUserBuySellButtonBuy.setOnClickListener{
            communicator.handleFragmentOnClick("Buy")
        }

        binding.fragmentUserBuySellButtonSell.setOnClickListener{
            communicator.handleFragmentOnClick("Sell")
        }
    }

    private fun initObservers() {
        viewModel.singleCurrency.observe(viewLifecycleOwner) { currency ->
            if(currency != null && userBalance != null){
                currencyPrice = currency.priceUsd.setScale(5, RoundingMode.HALF_UP)
                binding.fragmentUserBuySellTextCalculation.text = "${userBalance} x ${currencyPrice} "

                val userCurrencyValueUsd = userBalance!!.multiply(currencyPrice).setScale(2, RoundingMode.HALF_UP)
                binding.fragmentUserBuySellTextTotal.text = "Value: ${userCurrencyValueUsd} USD"
            }
        }
        viewModel.userCurrencyBalance.observe(viewLifecycleOwner) { retrievedUserCurrency ->
            if(retrievedUserCurrency !== null) {
                userBalance = retrievedUserCurrency.currencyAmount.toBigDecimal().setScale(4, RoundingMode.HALF_UP)
                binding.fragmentUserBuySellTextBalance.text = "${currencyParameter!!.capitalize()} Balance: $userBalance "
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Snackbar.make(
                        binding.root,
                        error,
                        Snackbar.LENGTH_LONG
                ).show()
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
                UserBuySellFragment().apply {
                    arguments = Bundle().apply {
                        putString("currency", param1)
                    }
                }
    }
}

interface FragmentClickCommunicator {
    fun handleFragmentOnClick(action: String)
}