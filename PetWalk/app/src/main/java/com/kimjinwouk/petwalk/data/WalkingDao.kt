package com.kimjinwouk.petwalk.data

import a.jinkim.calculate.model.Walking
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WalkingDao {

    @Query("SELECT * FROM Walking")
    fun getAll(): List<Walking>

    @Insert
    suspend fun insertWalk(walking: Walking)

    @Query("DELETE FROM Walking")
    fun deleteAll()

    @Delete
    fun deleteHistory(walking: Walking)


/*
    @Query("select * from Walking where result like :result LIMIT 1")
    fun findByResults(result : String) : Walking

    @Query("select * from Walking where result like :result")
    fun findByResult(result : String) : List<Walking>
 */
}