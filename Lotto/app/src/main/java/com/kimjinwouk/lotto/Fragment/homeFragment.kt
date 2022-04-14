package com.kimjinwouk.lotto.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.kimjinwouk.lotto.MainActivity
import com.kimjinwouk.lotto.R
import com.kimjinwouk.lotto.WebViewActivity

class homeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var tv_qr : TextView
    lateinit var tv_place : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        initListner()
    }

    private fun init(view: View)
    {
        tv_qr = view.findViewById(R.id.tv_qr)
        tv_place = view.findViewById(R.id.tv_place)
    }
    private fun initListner()
    {

        tv_qr.setOnClickListener {

            runQR()
        }
        tv_place.setOnClickListener {
            (activity as MainActivity).bnv_main.run{
                selectedItemId = R.id.place
            }
            //(activity as MainActivity).changeFragment((activity as MainActivity).placeFragment)
            //(activity as MainActivity).selectedView((activity as MainActivity).PLACE)
        }
    }

    private fun runQR()
    {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // 여러가지 바코드중에 특정 바코드 설정 가능
        integrator.setPrompt("QR 코드를 스캔하여 주세요.") // 스캔할 때 하단의 문구
        integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
        integrator.setBeepEnabled(false) // 바코드를 인식했을 때 삑 소리유무
        integrator.setBarcodeImageEnabled(true) // 스캔 했을 때 스캔한 이미지 사용여부
        integrator.setOrientationLocked(false)
        integrator.initiateScan() // 스캔
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // QR 코드를 찍은 결과를 변수에 담는다.

        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        Log.d("TTT", "QR 코드 체크")

        //결과가 있으면
        if (result != null) {
            // 컨텐츠가 없으면
            if (result.contents == null) {
                //토스트를 띄운다.
                Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // 컨텐츠가 있으면
            else {
                //토스트를 띄운다.
                Toast.makeText(context, "scanned" + result.contents, Toast.LENGTH_LONG).show()
                Log.d("TTT", "QR 코드 URL:${result.contents}")

                val nextIntent = Intent(context, WebViewActivity::class.java)
                nextIntent.putExtra("url",result.contents);
                startActivity(nextIntent)
            }
            // 결과가 없으면
        }
    }
}