package com.kimjinwouk.lotto.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kimjinwouk.lotto.DTO.winLotto
import com.kimjinwouk.lotto.Kakao.Document
import com.kimjinwouk.lotto.Kakao.KakaoData
import com.kimjinwouk.lotto.R

class ResultAdapter(private val items: ArrayList<winLotto>) : BaseAdapter() {


    override fun getCount(): Int = items.size

    override fun getItem(p0: Int): winLotto = items.elementAt(p0)

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val holder: ViewHolder
        var convertView = p1

        if (convertView == null) {
            convertView =
                LayoutInflater.from(p2?.context).inflate(R.layout.result_1st_item, p2, false)

            holder = ViewHolder()
            holder.Address = convertView?.findViewById(R.id.tv_address_value)
            holder.Name = convertView?.findViewById(R.id.name)
            holder.Type = convertView?.findViewById(R.id.tv_type_value)
            convertView?.tag = holder

        }
        else
        {
            holder = convertView.tag as ViewHolder
        }

        val item: winLotto = items.elementAt(p0)

        holder.Name!!.text = item.name
        holder.Address!!.text = item.address
        holder.Type!!.text = item.type

        return convertView!!
    }

    private class ViewHolder {
        var Name: TextView? = null
        var Type: TextView? = null
        var Address: TextView? = null
    }
}
