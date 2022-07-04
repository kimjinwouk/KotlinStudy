package com.kimjinwouk.petwalk.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kimjinwouk.petwalk.BaseFragment
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.home.HomeFragment

class ChatFragment : Fragment(R.layout.fragment_chat) {

    companion object: BaseFragment {
        override fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}