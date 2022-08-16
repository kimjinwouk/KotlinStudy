package com.nfc.ui.fragment

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.nfc.R
import com.nfc.databinding.FragmentReadBinding
import com.nfc.util.Util
import com.nfc.viewModel.nfcViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReadFragment : Fragment(R.layout.fragment_read) {

    //뷰바인딩
    private lateinit var binding: FragmentReadBinding

    // 뷰모델 생성
    private val viewModel by activityViewModels<nfcViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReadBinding.bind(view)
        binding.apply {
            var frameAnimation = nfcImageView?.getBackground() as AnimationDrawable
            nfcImageView?.post(object : Runnable {
                override fun run() {
                    frameAnimation.start()
                }
            })
        }
        viewModel.objRider.observe(viewLifecycleOwner) {
            it?.let {
                binding.titleTextView.text = "${it.RName}"+ Util.riderSituation(it.RSituation)
                binding.riderRpdaTextView.text = Util.formatTelNumber("${it.RPda}")
                binding.riderMisuTextView.text = Util.riderMisu(it.RiderID,it.Misu)
            }
        }
        viewModel.isReadEvent.observe(viewLifecycleOwner, Observer {
            if (!isAdded) {
                return@Observer
            }
            if (it) {
                viewModel.getRiderId(viewModel.readSerialNumber.value.toString(), 1)
                viewModel.isReadEvent.value = false
            }

        })
    }


}