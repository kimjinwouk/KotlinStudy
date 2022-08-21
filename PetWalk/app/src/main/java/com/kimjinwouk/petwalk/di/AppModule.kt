package com.kimjinwouk.petwalk.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.kimjinwouk.petwalk.data.AppDatabase
import com.kimjinwouk.petwalk.data.DBKey
import com.kimjinwouk.petwalk.util.Constants.Companion.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    // 데이터 베이스 추가
    @Singleton
    @Provides
    fun provideRunDatabase(app : Application) =
        Room.databaseBuilder(app,AppDatabase::class.java,"petwalk_db")
            .fallbackToDestructiveMigration() // 버전 변경 시 기존 데이터 삭제
            .build()


    // Dao 추가
    @Singleton
    @Provides
    fun provideRunDao(db : AppDatabase) = db.walkingDao()

    // SharedPreferences 추가
    @Singleton
    @Provides
    fun provideSharedPreferences(app: Application) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    // FirebaseAuth 추가
    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    // FirebaseStorage 추가
    @Singleton
    @Provides
    fun provideFirebaseStorage() = Firebase.storage


    // FusedLocationProviderClient 추가
    @Singleton
    @Provides
    fun providesFusedLocationProviderClient(
        @ApplicationContext context : Context
    ) = FusedLocationProviderClient(context)

    // DatabaseReference 추가
    @Singleton
    @Provides
    fun providesDatabaseReference(
    ) = Firebase.database.reference.child(DBKey.DB_USERS)



}