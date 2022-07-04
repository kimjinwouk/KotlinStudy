package com.kimjinwouk.petwalk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.kimjinwouk.petwalk.chat.ChatFragment
import com.kimjinwouk.petwalk.databinding.ActivityLoginBinding
import com.kimjinwouk.petwalk.databinding.ActivityMainBinding
import com.kimjinwouk.petwalk.home.HomeFragment
import com.kimjinwouk.petwalk.map.MapFragment
import com.kimjinwouk.petwalk.myinfo.MyInfoFragment

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var mapFragment: MapFragment
    private lateinit var myInfoFragment: MyInfoFragment
    private lateinit var chatFragment: ChatFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

    }


}