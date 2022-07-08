package com.kimjinwouk.petwalk

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kimjinwouk.petwalk.DB.AppDatabase
import com.kimjinwouk.petwalk.DB.DBKey
import com.kimjinwouk.petwalk.DB.Data

open class BaseActivity : AppCompatActivity() {

    public val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    public val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    public lateinit var userDB: DatabaseReference
    public lateinit var petRoomDB: AppDatabase


    public fun Log(context: Context, msg: String) {
        android.util.Log.d("${context::class.java.simpleName}", msg)
    }
    public fun Toast(context: Context, msg: String) {
        android.widget.Toast.makeText(context,msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    val data = Data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDB = Firebase.database.reference.child(DBKey.DB_USERS)
        petRoomDB = AppDatabase.getInstance(this)
    }

    override fun onResume() {
        super.onResume()

    }
}