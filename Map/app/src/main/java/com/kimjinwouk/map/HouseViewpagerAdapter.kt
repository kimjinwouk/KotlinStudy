package com.kimjinwouk.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kimjinwouk.map.databinding.ItemHouseDetailForViewpagerBinding

class HouseViewpagerAdapter(val itemClicked:(HouseModel) -> Unit) : ListAdapter<HouseModel, HouseViewpagerAdapter.ItemViewHolder>(diffUtil) {

    inner class ItemViewHolder(private val binding: ItemHouseDetailForViewpagerBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (houseModel: HouseModel){
            binding.titleTextView.text = houseModel.title
            binding.priceTextView.text = houseModel.price

            Glide.with(binding.thumbnailImageView)
                .load(houseModel.imgUrl)
                .into(binding.thumbnailImageView)

            binding.root.setOnClickListener{
                itemClicked(houseModel)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemHouseDetailForViewpagerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList[position])
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