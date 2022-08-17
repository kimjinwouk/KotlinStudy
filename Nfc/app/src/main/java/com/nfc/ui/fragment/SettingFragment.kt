package com.nfc.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.nfc.R
import com.nfc.data.Statistics
import com.nfc.databinding.FragmentSettingBinding
import com.nfc.databinding.FragmentStatisticsBinding
import com.nfc.util.Constants
import com.nfc.util.TimeAxisValueFormat
import com.nfc.viewModel.nfcViewModel
import com.qtec.nfc.ui.activity.LoginActivity
import com.qtec.nfc.ui.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment(R.layout.fragment_setting) {


    //뷰바인딩
    private lateinit var binding: FragmentSettingBinding

    // 뷰모델 생성
    private val viewModel by activityViewModels<nfcViewModel>()

    // Context
    private lateinit var _context: Context

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences


    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingBinding.bind(view)

        binding.apply {
            useridTextView.text = sharedPref.getString(Constants.SHARED_PREFERENCES_LOGINID,"")
            logoutButton.setOnClickListener {
                sharedPref.edit{
                    this.apply{
                        putString(Constants.SHARED_PREFERENCES_LOGINID,"")
                        putString(Constants.SHARED_PREFERENCES_PASSWORD,"")
                        putBoolean(Constants.SHARED_PREFERENCES_LOGINCHECKBOX,false)
                    }
                }
                startActivity(Intent(requireContext(),LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }
}