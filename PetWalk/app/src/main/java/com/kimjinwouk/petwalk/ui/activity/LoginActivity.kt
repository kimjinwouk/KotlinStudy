package com.kimjinwouk.petwalk.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.auth.FirebaseAuth
import com.kimjinwouk.petwalk.databinding.ActivityLoginBinding
import com.kimjinwouk.petwalk.util.Constants
import com.kimjinwouk.petwalk.viewmodel.walkViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    // 뷰바인딩
    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var auth: FirebaseAuth

    // 뷰모델 생성
    private val viewModel by viewModels<walkViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        Log.d(Constants.TAG,"LoginActivity_OnCreate")
        binding.signUpButton.setOnClickListener(this)
        binding.signInButton.setOnClickListener(this)

        viewModel.isLogin.observe(this, Observer {
            if (it) {
                //로그인 또는 회원가입이 이루어졌을 경우.
                android.widget.Toast.makeText(this, "로그인 성공", android.widget.Toast.LENGTH_SHORT)
                    .show()
                //로그인 완료 UI 변경 시점.
                //로그인이 완료된 후에는 Firebase에서 기타 외적인 데이터를 가지고 와야한다.
                //viewModel.getUserOnFirebase()
                startActivity(Intent(this, MainActivity::class.java))
            }
        })

        viewModel.isSignUp.observe(this, Observer {
            if (it) {
                viewModel.setUserOnFirebase(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                )
            }
        })

        viewModel.loginDataRealtimeDB.observe(this, Observer {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })



    }

    private fun SignIn() {
        viewModel.SignIn(
            binding.emailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun SignUp() {
        viewModel.SignUp(
            binding.emailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            //회원가입
            binding.signUpButton.id -> SignUp()
            //로그인
            binding.signInButton.id -> SignIn()
            else -> return
        }
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}