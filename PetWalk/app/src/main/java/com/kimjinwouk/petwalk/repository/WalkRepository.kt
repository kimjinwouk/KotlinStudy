package com.kimjinwouk.petwalk.repository

import a.jinkim.calculate.model.Walking
import androidx.lifecycle.LiveData
import com.kimjinwouk.petwalk.data.WalkingDao
import javax.inject.Inject

// 앱에서 사용하는 데이터와 그 데이터 통신을 하는 역할
class WalkRepository @Inject constructor(
    private val walkingDao: WalkingDao
) {

    val readAllData = walkingDao.getAll()

    suspend fun insertWalk(walk : Walking) = walkingDao.insertWalk(walk)
    suspend fun selectAll() = walkingDao.getAll()


    /*
    suspend fun insertRun(run : Run) = walkingDao.insertRun(run)
    suspend fun deleteRun(run : Run) = walkingDao.deleteRun(run)
    suspend fun updateRun(run : Run) = walkingDao.updateRun(run)
    fun deleteAllRun() = walkingDao.deleteAll()

    fun getAllRunsSortedByDate() = runDao.getAllRunsSortedByDate()
    fun getAllRunsSortedByTimeInMillis() = runDao.getAllRunsSortedByTimeInMillis()
    fun getAllRunsSortedByDistance() = runDao.getAllRunsSortedByDistance()
    fun getAllRunsSortedByAvgSpeed() = runDao.getAllRunsSortedByAvgSpeed()
    fun getAllRunsSortedByCaloriesBurned() = runDao.getAllRunsSortedByCaloriesBurned()

    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalTimeInMillis() = runDao.getTotalTimeInMillis()
    fun getTotalAvgSpeed() = runDao.getTotalAvgSpeed()
    fun getTotalCaloriesBurned() = runDao.getTotalCaloriesBurned()
    fun getMaxTimeInMillis() = runDao.getMaxTimeInMillis()
    fun getMaxDistanceInMillis() = runDao.getMaxDistance()
    fun getTotalRunning() = runDao.getTotalRunning()
    */

}