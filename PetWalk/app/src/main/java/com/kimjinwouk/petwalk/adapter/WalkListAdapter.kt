package com.kimjinwouk.petwalk.adapter

import a.jinkim.calculate.model.Walking
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.petwalk.databinding.WalkingRawBinding

class WalkListAdapter() : RecyclerView.Adapter<WalkListAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(private val binding: WalkingRawBinding)
        : RecyclerView.ViewHolder(binding.root){

        fun bind (walk: Walking){
            binding.textViewUid.text = walk.uid.toString()
            binding.textViewDate.text = walk.Date.toString()
            binding.textViewTime.text = walk.Time.toString()
            binding.textViewDistance.text = walk.Distance.toString()
            binding.imageViewNaverSnapshot.setImageBitmap(walk.Bitmap)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = WalkingRawBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        // 현재 아이템 넘겨줌
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    private fun dpToPx(context: Context, dp: Int) : Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),context.resources.displayMetrics).toInt()
    }

    // 데이터 변경
    fun submitList(list: List<Walking>) = differ.submitList(list)

    // 비동기 처리
    val differ = AsyncListDiffer(this, diffUtil)


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

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}