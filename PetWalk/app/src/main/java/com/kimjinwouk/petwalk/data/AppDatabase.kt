package com.kimjinwouk.petwalk.data

import a.jinkim.calculate.model.Walking
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kimjinwouk.petwalk.util.Converters


/* entities = 사용할 엔티티 선언, version = 엔티티 구조 변경 시 구분해주는 역할
   exportSchema = 스키마 내보내기 설정 */
@Database(entities = [Walking::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walkingDao(): WalkingDao
}