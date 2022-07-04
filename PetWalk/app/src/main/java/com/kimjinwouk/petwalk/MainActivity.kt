package com.kimjinwouk.petwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.kimjinwouk.petwalk.chat.ChatFragment
import com.kimjinwouk.petwalk.databinding.ActivityMainBinding
import com.kimjinwouk.petwalk.home.HomeFragment
import com.kimjinwouk.petwalk.map.MapFragment
import com.kimjinwouk.petwalk.myinfo.MyInfoFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var myInfoFragment: MyInfoFragment
    private lateinit var chatFragment: ChatFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        FragmentInit()
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(homeFragment)
                R.id.map -> replaceFragment(mapFragment)
                R.id.chatList -> replaceFragment(chatFragment)
                R.id.myInfo -> replaceFragment(myInfoFragment)
            }
            true
        }
    }

    //프로그먼트 초기화
    private fun FragmentInit() {
        homeFragment = HomeFragment()
        mapFragment = MapFragment()
        myInfoFragment = MyInfoFragment()
        chatFragment = ChatFragment()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }
}