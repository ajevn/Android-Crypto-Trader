package no.ajevn.cryptotrader.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.ajevn.cryptotrader.R
import no.ajevn.cryptotrader.data.CurrencyItem

class CurrencyItemListAdapter(val currencyList: List<CurrencyItem>, val listenerActivity: OnItemClickListener)
    : RecyclerView.Adapter<CurrencyItemListAdapter.CurrencyItemViewHolder>() {

    inner class CurrencyItemViewHolder(currencyItemView: View) : RecyclerView.ViewHolder(currencyItemView), View.OnClickListener{
        val currencyLayout: LinearLayout = currencyItemView.findViewById(R.id.currency_list_item_layout)
        val currencyIcon: ImageView = currencyItemView.findViewById(R.id.currency_list_item_icon)
        val currencyId: TextView = currencyItemView.findViewById(R.id.currency_list_item_text_id)
        val currencySymbol: TextView = currencyItemView.findViewById(R.id.currency_list_item_text_symbol)
        val currencyPrice: TextView = currencyItemView.findViewById(R.id.currency_list_item_text_price)
        val currencyChange24Hr: TextView = currencyItemView.findViewById(R.id.currency_list_item_text_change24Hr)
        init {
            currencyLayout.setOnClickListener(this)
        }
        //Passing id of clicked currency item to the listening activity
        override fun onClick(v: View?) {
            val currency = currencyList[adapterPosition].id
            listenerActivity.onItemClick(currency)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyItemViewHolder {
        val currencyItemView = LayoutInflater.from(parent.context).inflate(R.layout.currency_list_item, parent, false)
        return CurrencyItemViewHolder(currencyItemView)
    }


    override fun getItemCount(): Int {
        return currencyList.size
    }

    override fun onBindViewHolder(holder: CurrencyItemViewHolder, position: Int) {
        holder.currencyId.text = currencyList[position].id.capitalize()
        holder.currencySymbol.text = currencyList[position].symbol.capitalize()
        holder.currencyPrice.text =  "$" + currencyList[position].priceUsd.toString()
            if(currencyList[position].changePercent24Hr.toDouble() >= 0){
                holder.currencyChange24Hr.text = "+" + currencyList[position].changePercent24Hr.take(4) + "%"
                holder.currencyChange24Hr.setTextColor(getColor(holder.currencyChange24Hr.context, R.color.ticker_green_primary))
            } else {
                holder.currencyChange24Hr.text = currencyList[position].changePercent24Hr.take(4) + "%"
                holder.currencyChange24Hr.setTextColor(getColor(holder.currencyChange24Hr.context, R.color.ticker_red_primary))
            }
        Glide.with(holder.currencyIcon.context).load("https://static.coincap.io/assets/icons/" + currencyList[position].symbol.toLowerCase() + "@2x.png").into(holder.currencyIcon)
    }

    //Making sure MainActivity implements required methods to handle Item onClick functions data
    interface OnItemClickListener{
        fun onItemClick(currency: String)
    }
}