package com.kimjinwouk.petwalk.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.kimjinwouk.petwalk.R

class HomeFragment : Fragment(R.layout.fragment_home) {

    companion object: BaseFragment {
        override fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}