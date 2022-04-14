package com.kimjinwouk.lotto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.kimjinwouk.lotto.Fragment.homeFragment
import com.kimjinwouk.lotto.Fragment.placeFragment

class MainActivity : BaseActivity() {

    private val homeFragment by lazy { homeFragment() }
    private val placeFragment by lazy { placeFragment() }


    private val bnv_main: BottomNavigationView by lazy {
        findViewById(R.id.bnv_main)
    }

    private val fab_qr: FloatingActionButton by lazy {
        findViewById(R.id.fab_qr)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initListner();
        initNavigationBar()
    }

    private fun init()
    {

    }

    private fun initListner()
    {

        fab_qr.setOnClickListener {
            runQR()
        }

    }


    private fun runQR()
    {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // 여러가지 바코드중에 특정 바코드 설정 가능
        integrator.setPrompt("QR 코드를 스캔하여 주세요.") // 스캔할 때 하단의 문구
        integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
        integrator.setBeepEnabled(false) // 바코드를 인식했을 때 삑 소리유무
        integrator.setBarcodeImageEnabled(true) // 스캔 했을 때 스캔한 이미지 사용여부
        integrator.setOrientationLocked(false)
        integrator.initiateScan() // 스캔
    }





    private fun initNavigationBar() {
        bnv_main.run {
            setOnItemSelectedListener  {
                when (it.itemId) {
                    R.id.home -> {
                        changeFragment(homeFragment)
                    }
                    R.id.place -> {
                        changeFragment(placeFragment)
                    }
                }
                true
            }
            selectedItemId = R.id.home
        }
    }

    public fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment).commit()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // 컨텐츠가 있으면
            else {
                //토스트를 띄운다.
                Toast.makeText(this, "scanned" + result.contents, Toast.LENGTH_LONG).show()
                Log.d("TTT", "QR 코드 URL:${result.contents}")

                val nextIntent = Intent(this, WebViewActivity::class.java)
                nextIntent.putExtra("url",result.contents);
                startActivity(nextIntent)
            }
            // 결과가 없으면
        }
    }
}