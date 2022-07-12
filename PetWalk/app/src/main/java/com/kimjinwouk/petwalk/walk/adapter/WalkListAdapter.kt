package com.kimjinwouk.petwalk.walk.adapter

import a.jinkim.calculate.model.Walking
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.petwalk.databinding.WalkingRawBinding

class WalkListAdapter : ListAdapter<Walking, WalkListAdapter.ItemViewHolder>(diffUtil) {

    inner class ItemViewHolder(private val binding: WalkingRawBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (walk: Walking){
            binding.resultTextView.text = walk.ItemId.toString()


        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = WalkingRawBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private fun dpToPx(context: Context, dp: Int) : Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),context.resources.displayMetrics).toInt()
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<Walking>(){
            override fun areItemsTheSame(oldItem: Walking, newItem: Walking): Boolean {
                return oldItem.uid == newItem.uid

            }

            override fun areContentsTheSame(oldItem: Walking, newItem: Walking): Boolean {
                return oldItem == newItem
            }

        }
    }
}