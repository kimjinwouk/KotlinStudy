package com.kimjinwouk.petwalk.login

data class UserItemModel(
    val email : String,
    val nickName : String,
    val imageUri : String
    //유저 아이템에대한건 여기에 추가.

){
    constructor():this("","","")
}
