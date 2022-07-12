package com.kimjinwouk.petwalk.walk

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kimjinwouk.petwalk.BaseFragment
import com.kimjinwouk.petwalk.DB.Data
import com.kimjinwouk.petwalk.DB.Data.petWalkDB
import com.kimjinwouk.petwalk.MainActivity
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.databinding.FragmentWalkinglistBinding
import com.kimjinwouk.petwalk.walk.adapter.WalkListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalkingListFragment : Fragment(R.layout.fragment_walkinglist) {

    private lateinit var binding: FragmentWalkinglistBinding

    companion object : BaseFragment {
        override fun newInstance(): WalkingListFragment {
            return WalkingListFragment()
        }
    }

    private val recyclerAdapter = WalkListAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWalkinglistBinding.bind(view)
        binding.recyclerView.adapter = recyclerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        initData()
    }

    fun initData() {
        CoroutineScope(Dispatchers.Main).launch {
            /*
            * 데이터를 받았으면 가공이 필요하다.
            * ItemId를 하나로묶고 Location값을 배열형태로가져와야한다 그래야 한 아이템에 대해 지도에 뿌려줄수있다.
            * */

            recyclerAdapter.submitList(petWalkDB?.walkingDao()?.getAll())
            recyclerAdapter.notifyDataSetChanged()
        }
    }
    lateinit var mainActivity: MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }


}