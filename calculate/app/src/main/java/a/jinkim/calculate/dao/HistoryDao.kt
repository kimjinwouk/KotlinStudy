package a.jinkim.calculate.dao

import a.jinkim.calculate.model.History
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao{

    @Query("SELECT * FROM HISTORY")
    fun getAll(): List<History>

    @Insert
    fun insertHistory(history: History)

    @Query("DELETE FROM HISTORY")
    fun deleteAll()

    @Delete
    fun deleteHistory(history: History)

    @Query("select * from history where result like :result LIMIT 1")
    fun findByResults(result : String) : History

    @Query("select * from history where result like :result")
    fun findByResult(result : String) : List<History>
}