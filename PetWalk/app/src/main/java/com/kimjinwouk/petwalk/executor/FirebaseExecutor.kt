package com.kimjinwouk.petwalk.executor

import androidx.lifecycle.MutableLiveData
import com.kimjinwouk.petwalk.model.UserItemModel

interface FirebaseExecutor {

    //로그인
    fun onLogin(isLogin: MutableLiveData<Boolean>, email: String, password: String)
    //회원가입
    fun onSignUp(isLogin: MutableLiveData<Boolean>, email: String, password: String)
    //
    fun setUserOnFirebase(
        userProfile: MutableLiveData<UserItemModel>,
        email: String,
        password: String
    )

    fun getUserOnFirebase(loginUser: MutableLiveData<UserItemModel>): Boolean
    fun uploadProfileImage(
        selectedUri: String,
        loginUser: MutableLiveData<UserItemModel>,
        isChange: MutableLiveData<Boolean>
    )

}