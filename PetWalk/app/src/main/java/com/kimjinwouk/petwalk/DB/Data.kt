package com.kimjinwouk.petwalk.DB

import android.location.Location
import com.kimjinwouk.petwalk.login.UserItemModel

/*
* 싱글톤패턴 으로 유저데이터를 최초 한번받아 저장 후 앱이 종료되기 직전까지 사용.
* */
object Data {
    lateinit var loginUser : UserItemModel
    lateinit var petWalkDB : AppDatabase
    var myLocation : Location? = null



}