package com.kimjinwouk.petwalk.viewmodel

import a.jinkim.calculate.model.Walking
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.kimjinwouk.petwalk.model.UserItemModel
import com.kimjinwouk.petwalk.repository.WalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class walkViewModel @Inject constructor(
    private val walkRepository: WalkRepository
) : ViewModel() {

    var isLogin = MutableLiveData<Boolean>(false)
    var isSignUp = MutableLiveData<Boolean>(false)
    var loginDataRealtimeDB = MutableLiveData<UserItemModel>()

    //로그인
    fun SignIn(email: String, password: String) = walkRepository.SignIn(isLogin,email, password)

    //회원가입
    fun SignUp(email: String, password: String) = walkRepository.SignUp(isSignUp,email, password)

    //회원가입 후 Firebase 에서 기타 정보 저장.
    fun setUserOnFirebase(email: String, password: String) = walkRepository.setUserOnFirebase(loginDataRealtimeDB,email,password)

    //로그인 후 Firebase 에서 기타 정보 가져오기.
    fun getUserOnFirebase() = walkRepository.getUserOnFirebase(loginDataRealtimeDB)

    val walks: LiveData<List<Walking>>

    init {
        walks = walkRepository.readAllData
    }

    fun insertWalk(walk: Walking) = viewModelScope.launch {
        walkRepository.insertWalk(walk)
    }

    fun selectWalk() = viewModelScope.launch {
        walkRepository.selectAll()
    }

}