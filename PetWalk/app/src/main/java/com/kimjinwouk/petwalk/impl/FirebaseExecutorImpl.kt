package com.kimjinwouk.petwalk.impl

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.kimjinwouk.petwalk.executor.FirebaseExecutor
import com.kimjinwouk.petwalk.model.UserItemModel
import javax.inject.Inject


class FirebaseExecutorImpl @Inject constructor(
    val auth: FirebaseAuth,
    val userDB: DatabaseReference
) : FirebaseExecutor {

    //로그인
    override fun onLogin(isLogin: MutableLiveData<Boolean>, email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                isLogin.value = it.isSuccessful
            }


    }

    //회원가입
    override fun onSignUp(isSignUp: MutableLiveData<Boolean>, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                isSignUp.value = it.isSuccessful
            }
    }

    override fun getUserOnFirebase(loginUser: MutableLiveData<UserItemModel>) {


        userDB.child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        loginUser.value = snapshot.getValue(UserItemModel::class.java)!!
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

    }


    override fun setUserOnFirebase(
        userProfile: MutableLiveData<UserItemModel>,
        email: String,
        password: String
    ) {

        val saveData = UserItemModel(
            email = email,
            nickName = password,
            imageUri = ""
        )

        auth.currentUser?.let {
            userDB.child(it.uid)
                .setValue(saveData).addOnCompleteListener {
                    if (it.isSuccessful) {
                        userProfile.value = saveData
                    }
                }
        }
    }
}