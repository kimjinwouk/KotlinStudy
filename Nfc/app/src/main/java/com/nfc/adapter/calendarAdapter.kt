package com.nfc.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nfc.data.Profits
import com.nfc.databinding.EventItemHeaderBinding
import com.nfc.databinding.EventItemViewBinding
import com.nfc.util.Util
import com.nfc.util.Util.Companion.layoutInflater
import java.time.format.DateTimeFormatter

class calendarAdapter() : RecyclerView.Adapter<calendarAdapter.ViewHolder>() {

    val Profits = mutableListOf<Profits>()
    val TYPE_HEADER = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            EventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(Profits[position])
    }

    override fun getItemCount(): Int = Profits.size // 해더 +1

    inner class ViewHolder(val binding: EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(profit: Profits) {
            binding.typeTextView.text =  Util.calendarUnderListType(profit.Type)
            binding.cashTextView.text =  Util.moneyFormatToWon(profit.Cash)
        }
    }





}