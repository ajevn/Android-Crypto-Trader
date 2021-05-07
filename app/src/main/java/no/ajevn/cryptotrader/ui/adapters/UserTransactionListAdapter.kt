package no.ajevn.cryptotrader.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.database.entities.UserTransactions

class UserTransactionListAdapter(val userTransactionList: List<UserTransactions>)
    : RecyclerView.Adapter<UserTransactionListAdapter.UserTransactionListViewHolder>() {

    inner class UserTransactionListViewHolder(currencyItemView: View) : RecyclerView.ViewHolder(currencyItemView){
        val currencyLayout: LinearLayout = currencyItemView.findViewById(R.id.user_transaction_list_layout)
        val currencyIcon: ImageView = currencyItemView.findViewById(R.id.user_transaction_list_item_icon)
        val currencyOrderType: TextView = currencyItemView.findViewById(R.id.user_transaction_list_order_type)
        val currencyName: TextView = currencyItemView.findViewById(R.id.user_transaction_list_currency_name)
        val currencyAmount: TextView = currencyItemView.findViewById(R.id.user_transaction_list_currency_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTransactionListViewHolder {
        val currencyItemView = LayoutInflater.from(parent.context).inflate(R.layout.user_transaction_list_item, parent, false)
        return UserTransactionListViewHolder(currencyItemView)
    }

    override fun getItemCount(): Int {
        return userTransactionList.size
    }

    override fun onBindViewHolder(holder: UserTransactionListViewHolder, position: Int) {
        holder.currencyOrderType.text = userTransactionList[position].transactionType
        if(userTransactionList[position].transactionType.equals("Buy") || userTransactionList[position].transactionType.equals("Reward")){
            holder.currencyLayout.setBackgroundResource(R.color.ticker_green_primary)
        } else {
            holder.currencyLayout.setBackgroundResource(R.color.ticker_red_primary)
        }
        holder.currencyName.text = userTransactionList[position].currencyId.capitalize()
        holder.currencyAmount.text = userTransactionList[position].currencyAmount

        if(userTransactionList[position].transactionType != "Reward"){
            Glide.with(holder.currencyIcon.context).load("https://static.coincap.io/assets/icons/" + userTransactionList[position].currencySymbol.toLowerCase() + "@2x.png").into(holder.currencyIcon)
        } else {
            holder.currencyIcon.imageAlpha = 0
        }
    }
}