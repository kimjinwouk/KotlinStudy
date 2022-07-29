package com.kimjinwouk.petwalk.viewmodel

import a.jinkim.calculate.model.Walking
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    /*
    * Login
    * */
    //로그인
    fun SignIn(email: String, password: String) = walkRepository.SignIn(isLogin, email, password)

    //회원가입
    fun SignUp(email: String, password: String) = walkRepository.SignUp(isSignUp, email, password)

    //회원가입 후 Firebase 에서 기타 정보 저장.
    fun setUserOnFirebase(email: String, password: String) =
        walkRepository.setUserOnFirebase(loginDataRealtimeDB, email, password)

    /*
    * Main
    * */
    //로그인 후 Firebase 에서 기타 정보 가져오기.
    fun getUserOnFirebase() = viewModelScope.launch {
        isLogin.value = walkRepository.getUserOnFirebase(loginDataRealtimeDB)
    }


    /*
    * MyInfo
    * */
    var isChange = MutableLiveData<Boolean>(false)

    //프로필 이미지 업로드
    //이미지 업로드가 성공적으로 떨어지면 download uri를 loginDataRealtimeDB에 기록 후 옵저버가 감시하여 userData셋팅
    fun uploadProfileImage(selectedUri: String) = viewModelScope.launch {
        walkRepository.uploadProfileImage(selectedUri, loginDataRealtimeDB, isChange)
    }


    /*
    * WalkingList
    * */
    // 라이브데이터 묶어줌 (단일 옵저버에 추가 가능)
    val walks = MediatorLiveData<List<Walking>>()

    fun insertWalk(walk: Walking) = viewModelScope.launch {
        walkRepository.insertWalk(walk)
    }
    private val walksAll = walkRepository.selectWalk()

    init{
        walks.addSource(walksAll){
            it?.let{
                walks.value = it
            }
        }
    }


//    fun selectWalk()  = viewModelScope.launch {
//        walkRepository.selectAll()
//    }

}