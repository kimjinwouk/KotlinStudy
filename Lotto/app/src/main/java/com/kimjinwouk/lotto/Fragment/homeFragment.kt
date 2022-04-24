package com.kimjinwouk.lotto.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import com.kimjinwouk.lotto.MainActivity
import com.kimjinwouk.lotto.R
import com.kimjinwouk.lotto.WebViewActivity
import java.util.*

class homeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    lateinit var ll_qrcode : LinearLayout
    lateinit var ll_lottonumber : LinearLayout
    lateinit var ll_place : ConstraintLayout
    lateinit var tv_lotto : TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        initListner()

    }

    override fun onResume() {
        super.onResume()
        CreateLottoText()
    }

    override fun onPause() {
        super.onPause()
    }


    private fun init(view: View)
    {
        ll_qrcode = view.findViewById(R.id.ll_qrcode)
        ll_lottonumber = view.findViewById(R.id.ll_lottonumber)
        ll_place = view.findViewById(R.id.cl_place)
        tv_lotto = view.findViewById(R.id.tv_lotto)
    }
    private fun initListner()
    {

        ll_qrcode.setOnClickListener {
            runQR()
        }
        ll_place.setOnClickListener {
            (activity as MainActivity).bnv_main.run{
                selectedItemId = R.id.place
            }
        }
        ll_lottonumber.setOnClickListener{
            (activity as MainActivity).ShowFragmentLotto()
        }
    }

    private fun runLottoPick()
    {

    }

    private fun CreateLottoText()
    {
        //오늘시간에서 가장가까운 토요일 시간을 뺀 시간.
        val instance = Calendar.getInstance()
        val Today = Calendar.getInstance()

        val Result = Calendar.getInstance()

        val day = Today.get(Calendar.DAY_OF_WEEK)
        val date = Today.get(Calendar.HOUR)

        //DAY_OF_WEEK = 7 토요일
        //토요일 9시 이전이면 토요일 9시까지의 시간계산.
        //토요일 9시 이후면 다음주 토요일 9시 까지의 시간계산.
        when(day) {
            in 2..6 -> {
                Result.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY)
                Result.set(Calendar.HOUR_OF_DAY,21)
            }
            1 -> {
                Result.add(Calendar.DATE,7)
                Result.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY)
                Result.set(Calendar.HOUR_OF_DAY,21)
            }
            7 -> when (date) {
                in 0..20 -> {
                    Result.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    Result.set(Calendar.HOUR_OF_DAY, 21)
                }
                else -> {
                    Result.add(Calendar.DATE,7)
                    Result.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY)
                    Result.set(Calendar.HOUR_OF_DAY,21)
                }
            }
        }
        val hour = (getIgnoredTimeDays(Result.timeInMillis) - getIgnoredTimeDays(Today.timeInMillis)) / (60*60*1000)


        tv_lotto.text = "로또 발표까지 \n"+hour+"시간 남았습니다"
    }

    private fun getIgnoredTimeDays(time: Long) : Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.MINUTE,0)
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }.timeInMillis
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
                //Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // 컨텐츠가 있으면
            else {
                //토스트를 띄운다.
                //Toast.makeText(context, "scanned" + result.contents, Toast.LENGTH_LONG).show()
                //Log.d("TTT", "QR 코드 URL:${result.contents}")

                val nextIntent = Intent(context, WebViewActivity::class.java)
                nextIntent.putExtra("url",result.contents);
                startActivity(nextIntent)
            }
            // 결과가 없으면
        }
    }
}