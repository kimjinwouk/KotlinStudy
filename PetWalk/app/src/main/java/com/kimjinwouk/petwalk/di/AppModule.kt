package com.kimjinwouk.petwalk.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kimjinwouk.petwalk.data.AppDatabase
import com.kimjinwouk.petwalk.util.Constants.Companion.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    // SharedPreferences 추가
    @Singleton
    @Provides
    fun provideFirebaseAuth() = Firebase.auth


}