package com.kimjinwouk.lotto

import android.content.Context
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
import com.kimjinwouk.lotto.Fragment.lottonumberFragment


class MainActivity : BaseActivity() {

    public val homeFragment by lazy { homeFragment() }
    public val placeFragment by lazy { placeFragment() }
    public val lottonumberFragment by lazy { lottonumberFragment() }

    public val bnv_main: BottomNavigationView by lazy {
        findViewById(R.id.bnv_main)
    }

    private val fab_qr: FloatingActionButton by lazy {
        findViewById(R.id.fab_qr)
    }

    lateinit var m_Context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initListner()
        initNavigationBar()
    }


    private fun init() {
        m_Context = this
    }

    private fun initListner() {

        fab_qr.setOnClickListener {
            runQR()
        }

    }


    private fun runQR() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // 여러가지 바코드중에 특정 바코드 설정 가능
        integrator.setPrompt("QR 코드를 스캔하여 주세요.") // 스캔할 때 하단의 문구
        integrator.setCameraId(0) // 0은 후면 카메라, 1은 전면 카메라
        integrator.setBeepEnabled(false) // 바코드를 인식했을 때 삑 소리유무
        integrator.setBarcodeImageEnabled(true) // 스캔 했을 때 스캔한 이미지 사용여부
        integrator.setOrientationLocked(false)
        integrator.initiateScan() // 스캔
    }


    var didHomeAction : Boolean = false;
    override fun showPermissionGranted(permission: String) {
        //didHomeAction = true;

        ShowFragmentPlace()

        //selectedView(PLACE)
        //bnv_main.run {
        //    selectedItemId = R.id.place
        //}

    }


    override fun showPermissionDenied(permission: String, isPermanentlyDenied: Boolean) {
        //changeFragment(homeFragment)
        bnv_main.run {
            selectedItemId = R.id.home
        }
    }


    private fun initNavigationBar() {
        bnv_main.run {
            setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        ShowFragmentHome()
                    }
                    R.id.place -> {
                        requestPermission();

                    }
                }
                didHomeAction = false
                true
            }
            selectedItemId = R.id.home

        }
    }

    public fun ShowFragmentHome()
    {

        val fragmenthome: Fragment? = supportFragmentManager.findFragmentByTag("Home")
        if(fragmenthome == null)
        {
            supportFragmentManager.beginTransaction().add(R.id.fl_container,homeFragment,"Home").commit()
        }

        if(homeFragment != null) supportFragmentManager.beginTransaction().show(homeFragment).commit();
        if(placeFragment != null) supportFragmentManager.beginTransaction().hide(placeFragment).commit();
        if(lottonumberFragment != null) supportFragmentManager.beginTransaction().hide(lottonumberFragment).commit();
    }

    public fun ShowFragmentPlace()
    {
        startLocationUpdates()
        val fragmenthome: Fragment? = supportFragmentManager.findFragmentByTag("Place")
        if(fragmenthome == null)
        {
            supportFragmentManager.beginTransaction().add(R.id.fl_container,placeFragment,"Place").commit()
        }
        if(placeFragment != null) supportFragmentManager.beginTransaction().show(placeFragment).commit();
        if(homeFragment != null) supportFragmentManager.beginTransaction().hide(homeFragment).commit();
        if(lottonumberFragment != null) supportFragmentManager.beginTransaction().hide(lottonumberFragment).commit();
    }



    override fun CallbackLocate()
    {
        placeFragment.setLocate()
    }

    public fun ShowFragmentLotto()
    {
        val intent = Intent(this, LottoActivity::class.java)
        startActivity(intent)
    }

    public fun ShowFragmentResult()
    {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    /*
    public fun changeFragment(fragment: Fragment) {

        if(fragment.equals(homeFragment))
        { // 홈을 선택했을 경우
            val fragmenthome: Fragment? = supportFragmentManager.findFragmentByTag("Home")
            if (fragmenthome != null) {
                supportFragmentManager.beginTransaction().show(homeFragment).commit();
                //supportFragmentManager.beginTransaction().hide(placeFragment).commit();
                return
            }
        }
        else if(fragment.equals(placeFragment)){
            val fragmentplace: Fragment? = supportFragmentManager.findFragmentByTag("Place")

            if (fragmentplace != null) {
                supportFragmentManager.beginTransaction().show(placeFragment).commit();
                //supportFragmentManager.beginTransaction().hide(homeFragment).commit();
                return
            }
        }
        else if(fragment.equals(placeFragment)){
            val fragmentplace: Fragment? = supportFragmentManager.findFragmentByTag("Place")

            if (fragmentplace != null) {
                supportFragmentManager.beginTransaction().show(placeFragment).commit();
                //supportFragmentManager.beginTransaction().hide(homeFragment).commit();
                return
            }
        }

        supportFragmentManager.beginTransaction().add(R.id.fl_container, fragment, if (fragment.equals(homeFragment)) "Home" else "Place").commit();
        supportFragmentManager.beginTransaction().show(fragment).commit();

    }
    */

    override fun permissionResult(int: Int) {
        when (int) {
            HOME -> selectedView(int)
            RUN -> ShowFragmentPlace()
        }

    }

    public fun selectedView(int: Int) {
        bnv_main.run {
            selectedItemId = when (int) {
                HOME -> R.id.home
                else -> R.id.place
            }
        }
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
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
            // 컨텐츠가 있으면
            else {
                //토스트를 띄운다.
                //Toast.makeText(this, "scanned" + result.contents, Toast.LENGTH_LONG).show()
                //Log.d("TTT", "QR 코드 URL:${result.contents}")

                val nextIntent = Intent(this, WebViewActivity::class.java)
                nextIntent.putExtra("url", result.contents);
                startActivity(nextIntent)
            }
            // 결과가 없으면
        }
    }
}