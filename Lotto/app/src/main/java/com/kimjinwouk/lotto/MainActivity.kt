package com.kimjinwouk.lotto

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kimjinwouk.lotto.Fragment.homeFragment
import com.kimjinwouk.lotto.Fragment.placeFragment

class MainActivity : BaseActivity() {

    private val homeFragment by lazy { homeFragment() }
    private val placeFragment by lazy { placeFragment() }


    private val bnv_main: BottomNavigationView by lazy {
        findViewById(R.id.bnv_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initNavigationBar()
    }

    private fun init()
    {

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

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fl_container, fragment).commit()
    }

}