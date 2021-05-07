package no.ajevn.cryptotrader.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.UserPortfolioViewModel
import no.ajevn.cryptotrader.databinding.FragmentUserScoreBinding
import java.math.BigDecimal

class UserScoreFragment : Fragment(R.layout.fragment_user_score) {

    private lateinit var binding: FragmentUserScoreBinding
    private val viewModel: UserPortfolioViewModel by viewModels()
    private var userId: Long? = null
    private var userBalance: BigDecimal? = null

    companion object {
        fun newInstance() = UserScoreFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserScoreBinding.bind(view)

        // Init view model
        val preferences = requireActivity().getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        userId = preferences.getLong("current_user_id", 0)

        viewModel.init(requireContext(), userId!!)
        initObservers()
    }

    private fun initObservers() {
        viewModel.error.observe(requireActivity(), { error ->
            if(error.isNotEmpty()){
                Snackbar.make(binding.root, error, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") { viewModel.init(requireContext(), userId!!) }
                    .show()
            }
        })
        viewModel.updatedUserTotalBalance.observe(requireActivity(), { updatedUserBalance ->
            userBalance = updatedUserBalance
            binding.twFragmentUserScore.text = "Score: ${userBalance.toString()} USD"
        })

        viewModel.updatedUserChange.observe(requireActivity(), { updatedUserBalanceChange ->
            binding.twFragmentUserScoreChange.text = "$updatedUserBalanceChange"
            if(updatedUserBalanceChange.startsWith("+")){
                binding.twFragmentUserScoreChange.setTextColor(getColor(requireContext(), R.color.ticker_green_primary))
            } else {
                binding.twFragmentUserScoreChange.setTextColor(getColor(requireContext(), R.color.ticker_red_primary))
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val preferences = requireActivity().getSharedPreferences("crypto_trader_shared_preferences", Context.MODE_PRIVATE)
        userId = preferences.getLong("current_user_id", 0)
        viewModel.loadAllUserCurrenciesAndUserBalance(userId!!)
    }
}