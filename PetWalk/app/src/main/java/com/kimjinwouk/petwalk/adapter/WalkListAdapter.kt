package com.kimjinwouk.petwalk.adapter

import a.jinkim.calculate.model.Walking
import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.petwalk.databinding.EventItemViewBinding
import com.kimjinwouk.petwalk.util.PetWalkUtil.Companion.layoutInflater
import java.time.format.DateTimeFormatter

class WalkListAdapter() : RecyclerView.Adapter<WalkListAdapter.Example5FlightsViewHolder>() {

    val walks = mutableListOf<Walking>()

    private val formatter = DateTimeFormatter.ofPattern("HH시 mm분 ")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example5FlightsViewHolder {
        return Example5FlightsViewHolder(
            EventItemViewBinding.inflate(parent.context.layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(viewHolder: Example5FlightsViewHolder, position: Int) {
        viewHolder.bind(walks[position])
    }

    override fun getItemCount(): Int = walks.size

    inner class Example5FlightsViewHolder(val binding: EventItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(walk: Walking) {

            binding.mapImageView.setImageBitmap(walk.Bitmap)
            binding.distanceTextView.text = "이동거리 : ${walk.Distance}"
            binding.regDateTextView.text = "시작시간 ${walk.Date.format(formatter)}"
            binding.walkingTimeTextView.text = "산책시간 ${calWalkingTime(walk.Time)}"
        }
    }

    private fun calWalkingTime(Time: String): String {
        val seconds = Time.toInt() / 1000
        val minutes = Time.toInt() / (1000 * 60)
        val hours = Time.toInt() / (1000 * 60 * 60)
        var ret = ""
        ret = if (hours > 0) {            hours.toString() + "시"        } else {            ""        }
        ret += if (minutes > 0) {            minutes.toString() + "분"        } else {            ""        }
        ret += if (seconds > 0) {            seconds.toString() + "초"        } else {            ""        }

        return ret
    }
}