package com.kimjinwouk.petwalk.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kimjinwouk.petwalk.BaseFragment
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.home.HomeFragment

class MapFragment : Fragment(R.layout.fragment_map) {

    companion object: BaseFragment {
        override fun newInstance(): MapFragment {
            return MapFragment()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}