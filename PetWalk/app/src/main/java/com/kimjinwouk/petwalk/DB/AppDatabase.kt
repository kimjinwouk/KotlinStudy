package com.kimjinwouk.petwalk.DB

import a.jinkim.calculate.dao.WalkingDao
import a.jinkim.calculate.model.Walking
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Walking::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walkingDao(): WalkingDao


    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context, AppDatabase::class.java, "PetWalkDB"
            ).build()


        }
    }


}