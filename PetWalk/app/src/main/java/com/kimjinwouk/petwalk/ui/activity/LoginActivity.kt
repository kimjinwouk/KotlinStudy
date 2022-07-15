package com.kimjinwouk.petwalk.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kimjinwouk.petwalk.R
import com.kimjinwouk.petwalk.databinding.ActivityLoginBinding
import com.kimjinwouk.petwalk.model.UserItemModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.signUpButton.setOnClickListener(this)
        binding.signInButton.setOnClickListener(this)

    }

    private fun SignIn() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        //로그인
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    successSignIn()
                } else {
                    Toast(this, getString(R.string.SignInFailMsg))
                }
            }

    }

    override fun onStart() {
        super.onStart()
        signCheck()
    }

    private fun signCheck() {
        if (auth.currentUser != null) {
            getUserData()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun successSignIn() {
        if (auth.currentUser == null) {
            Toast(this, getString(R.string.SignInFailMsg))
            return
        }
        Toast(this, getString(R.string.SignInSuccessMsg))
        getUserData()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun getUserData() {
        userDB.child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val UserItemModel = snapshot.getValue(UserItemModel::class.java)
                    UserItemModel ?: return
                    data.loginUser = UserItemModel
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun SignUp() {

        //회원가입
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    successSignUp()
                } else {
                    Toast(this, getString(R.string.SignUpFailMsg))

                }
            }
    }

    private fun successSignUp() {
        //회원가입에 성공했을경우 해당 회원의 정보 저장.

        //유저의 디비를 가지고온다.
        //userDB = Firebase.database.reference.child(DB_USERS) BaseActivity로이동

        //가지고 온 유저디비에서 내정보를 가지고와 아래 닉네임타입에 회원가입시 들어간 닉네임을 저장.

        val UserItemModel = UserItemModel(
            email = binding.emailEditText.text.toString(),
            nickName = binding.nickEditText.text.toString(),
            imageUri = ""
        )


        auth.currentUser?.let {
            userDB.child(it.uid)
                .setValue(UserItemModel)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        Toast(this, getString(R.string.SignUpSuccessMsg))
                    } else {
                        Toast(this, getString(R.string.SignUpFailMsg))
                    }
                }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding.signUpButton.id -> SignUp()
            binding.signInButton.id -> SignIn()
            else -> return
        }

    }

}