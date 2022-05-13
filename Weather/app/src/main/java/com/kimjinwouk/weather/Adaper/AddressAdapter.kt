package com.kimjinwouk.weather.Adaper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kimjinwouk.weather.Data.ItemAddress
import com.kimjinwouk.weather.R

class AddressAdapter(private val items: List<ItemAddress>) : BaseAdapter() {


    override fun getCount(): Int = items.size

    override fun getItem(p0: Int): ItemAddress = items.elementAt(p0)

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val holder: ViewHolder
        var convertView = p1

        if (convertView == null) {
            convertView =
                LayoutInflater.from(p2?.context).inflate(R.layout.address_item, p2, false)

            holder = ViewHolder()
            holder.Address = convertView?.findViewById(R.id.tv_address)
            convertView?.tag = holder

        }
        else
        {
            holder = convertView.tag as ViewHolder
        }

        val item: String = items.elementAt(p0).address_full


        holder.Address!!.text = item


        return convertView!!
    }

    private class ViewHolder {
        var Address: TextView? = null
    }
}
