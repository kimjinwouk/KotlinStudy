package com.kimjinwouk.map

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kimjinwouk.map.databinding.ItemHouseBinding
import com.kimjinwouk.map.databinding.ItemHouseDetailForViewpagerBinding

class HouseListAdapter : ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(diffUtil) {

    inner class ItemViewHolder(private val binding: ItemHouseBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (houseModel: HouseModel){
            binding.titleTextView.text = houseModel.title
            binding.priceTextView.text = houseModel.price

            Glide.with(binding.thumbnailImageView)
                .load(houseModel.imgUrl)
                .transform(CenterCrop(),RoundedCorners(dpToPx(binding.thumbnailImageView.context,30)))
                .into(binding.thumbnailImageView)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemHouseBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private fun dpToPx(context: Context, dp: Int) : Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),context.resources.displayMetrics).toInt()
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<HouseModel>(){
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id

            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }
}