package com.kimjinwouk.petwalk.adapter

import a.jinkim.calculate.model.Walking
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.petwalk.databinding.EventItemViewBinding
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.layoutInflater
import java.time.format.DateTimeFormatter

class WalkListAdapter() : RecyclerView.Adapter<WalkListAdapter.Example5FlightsViewHolder>() {

    val flights = mutableListOf<Walking>()

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

        fun bind(flight: Walking) {
            binding.itemFlightDateText.apply {
                text = formatter.format(flight.Date)
            }

            binding.itemDepartureAirportCodeText.text = flight.Time
            binding.itemDepartureAirportCityText.text = flight.Distance.toString()

            binding.itemDestinationAirportCodeText.text = flight.uid.toString()
            binding.itemDestinationAirportCityText.text = flight.Locations.toString()
        }
    }
}