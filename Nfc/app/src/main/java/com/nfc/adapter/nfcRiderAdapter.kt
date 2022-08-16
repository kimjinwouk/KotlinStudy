package com.nfc.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nfc.data.Rider
import com.nfc.databinding.RideritemViewBinding
import com.nfc.util.Util


class nfcRiderAdapter(private val itemClickedListener:(Rider) -> Unit) : RecyclerView.Adapter<nfcRiderAdapter.riderViewHolder>() {

    val riders = mutableListOf<Rider>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): riderViewHolder {
        return riderViewHolder(
            RideritemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }



    override fun onBindViewHolder(viewHolder: riderViewHolder, position: Int) {
        viewHolder.bind(riders[position])
    }

    override fun getItemCount(): Int = riders.size

    inner class riderViewHolder(val binding: RideritemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rider: Rider) {

            binding.rnameTextView.text = rider.RName
            binding.rpdaTextView.text = Util.formatTelNumber(rider.RPda)
            binding.root.setOnClickListener{
                itemClickedListener(rider)
            }

        }
    }
}