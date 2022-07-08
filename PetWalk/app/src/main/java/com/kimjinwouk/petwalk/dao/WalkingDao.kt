package a.jinkim.calculate.dao

import a.jinkim.calculate.model.Walking
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WalkingDao {

    @Query("SELECT * FROM Walking")
    fun getAll(): List<Walking>

    @Insert
    fun insertWalk(walking: Walking)

    @Query("DELETE FROM Walking")
    fun deleteAll()

    @Delete
    fun deleteHistory(walking: Walking)

    @Query("SELECT ItemId FROM Walking ORDER BY ItemId desc LIMIT 1 ")
    fun getId(): Int

/*
    @Query("select * from Walking where result like :result LIMIT 1")
    fun findByResults(result : String) : Walking

    @Query("select * from Walking where result like :result")
    fun findByResult(result : String) : List<Walking>
 */
}