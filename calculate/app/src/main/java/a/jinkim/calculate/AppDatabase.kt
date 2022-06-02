package a.jinkim.calculate

import a.jinkim.calculate.dao.HistoryDao
import a.jinkim.calculate.model.History
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun historydao(): HistoryDao
}