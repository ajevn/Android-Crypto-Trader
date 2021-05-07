package no.ajevn.cryptotrader.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.viewModels.UserCurrenciesWithPrice
import java.math.RoundingMode

class UserPortfolioListAdapter(val userCurrenciesList: ArrayList<UserCurrenciesWithPrice>)
    : RecyclerView.Adapter<UserPortfolioListAdapter.UserPortfolioViewHolder>() {

    inner class UserPortfolioViewHolder(userPortfolioView: View) : RecyclerView.ViewHolder(userPortfolioView){
        val currencyIcon: ImageView = userPortfolioView.findViewById(R.id.user_portfolio_list_item_icon)
        val currencyId: TextView = userPortfolioView.findViewById(R.id.user_portfolio_list_item_text_symbol)
        val currencySymbol: TextView = userPortfolioView.findViewById(R.id.user_portfolio_list_item_text_symbol)
        val currencyAmount: TextView = userPortfolioView.findViewById(R.id.user_portfolio_list_item_text_currency_amount)
        val currencyValue: TextView = userPortfolioView.findViewById(R.id.user_portfolio_list_item_text_currency_value)
        val currencyTotalUsd: TextView = userPortfolioView.findViewById(R.id.user_portfolio_list_item_text_currency_total_usd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPortfolioViewHolder {
        val currencyItemView = LayoutInflater.from(parent.context).inflate(R.layout.user_portfolio_list_item, parent, false)
        return UserPortfolioViewHolder(currencyItemView)
    }


    override fun getItemCount(): Int {
        return userCurrenciesList.size
    }

    override fun onBindViewHolder(holder: UserPortfolioViewHolder, position: Int) {
        holder.currencyId.text = userCurrenciesList[position].currencyId
        holder.currencySymbol.text = userCurrenciesList[position].currencySymbol
        holder.currencyAmount.text =  "Amount: ${userCurrenciesList[position].currencyAmount}"

        val currencyPrice = userCurrenciesList[position].updatedCurrencyPrice.setScale(6, RoundingMode.HALF_UP)
        holder.currencyValue.text =  "Price: ${currencyPrice}"

        val totalValue = currencyPrice.multiply(userCurrenciesList[position].currencyAmount.toBigDecimal())
        totalValue.setScale(2, RoundingMode.HALF_UP)
        holder.currencyTotalUsd.text =  "${totalValue}"

        Glide.with(holder.currencyIcon.context).load("https://static.coincap.io/assets/icons/" + userCurrenciesList[position].currencySymbol.toLowerCase() + "@2x.png").into(holder.currencyIcon)
    }
}