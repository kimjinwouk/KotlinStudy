package com.kimjinwouk.lotto.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.lotto.Kakao.Document
import com.kimjinwouk.lotto.Kakao.KakaoData
import com.kimjinwouk.lotto.R

class BottomSheetAdapter(private val items: List<Document>) : BaseAdapter() {


    override fun getCount(): Int = items.size

    override fun getItem(p0: Int): Document = items[p0]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val holder: ViewHolder
        var convertView = p1

        if (convertView == null) {
            convertView =
                LayoutInflater.from(p2?.context).inflate(R.layout.bottomsheet_list_item, p2, false)

            holder = ViewHolder()
            holder.Address = convertView?.findViewById(R.id.address)
            holder.Name = convertView?.findViewById(R.id.name)
            holder.Distance = convertView?.findViewById(R.id.distance)
            convertView?.tag = holder

        }
        else
        {
            holder = convertView.tag as ViewHolder
        }

        val item: Document = items[p0]

        holder.Name!!.text = item.place_name
        holder.Address!!.text = item.address_name
        holder.Distance!!.text = item.distance

        return convertView!!
    }

    private class ViewHolder {
        var Name: TextView? = null
        var Distance: TextView? = null
        var Address: TextView? = null
    }
}
