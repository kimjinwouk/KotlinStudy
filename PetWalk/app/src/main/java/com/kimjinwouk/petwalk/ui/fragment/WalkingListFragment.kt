package com.kimjinwouk.petwalk.ui.fragment

import a.jinkim.calculate.model.Walking
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.adapter.WalkListAdapter
import com.kimjinwouk.petwalk.databinding.FragmentWalkinglistBinding
import com.kimjinwouk.petwalk.viewmodel.walkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalkingListFragment : Fragment(R.layout.fragment_walkinglist) {

    // 전역 변수로 바인딩 객체 선언
    private var _binding: FragmentWalkinglistBinding? = null

    // 매번 null 체크를 할 필요 없이 편의성을 위해 바인딩 변수 재 선언
    private val binding get() =_binding!!

    // 뷰모델 생성
    private val viewModel by viewModels<walkViewModel>()

    // WalkListAdapter 선언
    private val recyclerAdapter = WalkListAdapter()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 뷰바인딩
        _binding = FragmentWalkinglistBinding.bind(view)
        binding.apply {
            //Layout에서 줘야할 모든 옵션 적용.
            
            //어뎁터 연결
            recyclerView.adapter = recyclerAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())



        }


        viewModel.walks.observe(requireActivity()){
            recyclerAdapter.submitList(it)
        }


    }
}