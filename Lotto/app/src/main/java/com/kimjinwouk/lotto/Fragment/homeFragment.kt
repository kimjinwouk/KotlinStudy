package com.kimjinwouk.lotto.Fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.kimjinwouk.lotto.R

class homeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var tv_qr : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        initListner()
    }

    private fun init(view: View)
    {
        tv_qr = view.findViewById(R.id.tv_qr)
    }
    private fun initListner()
    {

        tv_qr.setOnClickListener {
            runQR()
        }
    }

    private fun runQR()
    {
        val integrator = IntentIntegrator(requireContext() as Activity?)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // 여러가지 바코드중에 특정 바코드 설정 가능
        integrator.setPrompt("QR 코드를 스캔하여 주세요.") // 스캔할 때 하단의 문구
        integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
        integrator.setBeepEnabled(false) // 바코드를 인식했을 때 삑 소리유무
        integrator.setBarcodeImageEnabled(true) // 스캔 했을 때 스캔한 이미지 사용여부
        integrator.setOrientationLocked(false)
        integrator.initiateScan() // 스캔
    }
}