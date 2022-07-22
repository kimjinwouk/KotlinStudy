package com.kimjinwouk.petwalk.model

data class UserItemModel(
    var email : String,
    var nickName : String,
    var imageUri : String
    //유저 아이템에대한건 여기에 추가.

){
    constructor():this("","","")
}
