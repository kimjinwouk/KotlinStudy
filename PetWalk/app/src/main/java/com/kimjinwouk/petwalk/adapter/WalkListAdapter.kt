package com.kimjinwouk.petwalk.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.petwalk.databinding.EventItemViewBinding
import com.kimjinwouk.petwalk.ui.fragment.WalkingListFragment
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.getColorCompat
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.layoutInflater
import java.time.format.DateTimeFormatter

class WalkListAdapter() : RecyclerView.Adapter<WalkListAdapter.Example5FlightsViewHolder>() {

    val flights = mutableListOf<WalkingListFragment.Flight>()

    private val formatter = DateTimeFormatter.ofPattern("EEE'\n'dd MMM'\n'HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example5FlightsViewHolder {
        return Example5FlightsViewHolder(
            EventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: Example5FlightsViewHolder, position: Int) {
        viewHolder.bind(flights[position])
    }

    override fun getItemCount(): Int = flights.size

    inner class Example5FlightsViewHolder(val binding: EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flight: WalkingListFragment.Flight) {
            binding.itemFlightDateText.apply {
                text = formatter.format(flight.time)
                setBackgroundColor(itemView.context.getColorCompat(flight.color))
            }

            binding.itemDepartureAirportCodeText.text = flight.departure.code
            binding.itemDepartureAirportCityText.text = flight.departure.city

            binding.itemDestinationAirportCodeText.text = flight.destination.code
            binding.itemDestinationAirportCityText.text = flight.destination.city
        }
    }
}