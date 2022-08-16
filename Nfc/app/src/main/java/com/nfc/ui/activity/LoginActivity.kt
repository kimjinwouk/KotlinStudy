package com.qtec.nfc.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.nfc.databinding.ActivityLoginBinding
import com.nfc.util.Constants.Companion.SHARED_PREFERENCES_USERID
import com.nfc.viewModel.nfcViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), View.OnClickListener {

    // 뷰바인딩
    private lateinit var binding: ActivityLoginBinding

    // 뷰모델 생성
    private val viewModel by viewModels<nfcViewModel>()

    // SharedPreferences 주입
    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.apply {
            signInButton.setOnClickListener(this@LoginActivity)
        }

        viewModel.objUser.observe(this){
            it?.let{
                if(it.USERID.toInt() > 0){
                    sharedPref.edit{
                        this.putString(SHARED_PREFERENCES_USERID,it.USERID)
                    }
                    startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun SignIn() {
        viewModel.Login(binding.userIdEditText.text.toString(),
            binding.passwordEditText.text.toString())
    }
    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.signInButton.id -> SignIn()
            else -> return
        }
    }
}
