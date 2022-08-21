package com.kimjinwouk.petwalk.repository

import a.jinkim.calculate.model.Walking
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.kimjinwouk.petwalk.data.WalkingDao
import com.kimjinwouk.petwalk.impl.FirebaseExecutorImpl
import com.kimjinwouk.petwalk.model.UserItemModel
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject


// 앱에서 사용하는 데이터와 그 데이터 통신을 하는 역할
class WalkRepository @Inject constructor(
    private val walkingDao: WalkingDao,
    private val auth: FirebaseAuth,
    private val userDB: DatabaseReference,
    private val firebaseExecutorImpl: FirebaseExecutorImpl
) {



    //room 아래와 같이 진행
    suspend fun insertWalk(walk : Walking) = walkingDao.insertWalk(walk)

    fun selectWalk() = walkingDao.getAll()



    //fun selectAll() : Flow<List<Walking>> = walkingDao.getAll()

    fun SignIn( isLogin : MutableLiveData<Boolean>, email : String, password : String){
        firebaseExecutorImpl.onLogin(isLogin,email,password)
    }
    fun SignUp( isSignUp : MutableLiveData<Boolean>, email : String, password : String){
        firebaseExecutorImpl.onSignUp(isSignUp,email,password)
    }
    fun setUserOnFirebase(
        userProfile: MutableLiveData<UserItemModel>,
        email: String,
        password: String
    ) {
        firebaseExecutorImpl.setUserOnFirebase(userProfile,email,password)
    }

    fun getUserOnFirebase(loginUser: MutableLiveData<UserItemModel>) : Boolean {
        return firebaseExecutorImpl.getUserOnFirebase(loginUser)
    }

    fun  uploadProfileImage(
        selectedUri: String,
        loginUser: MutableLiveData<UserItemModel>,
        isChange: MutableLiveData<Boolean>
    ) {
        firebaseExecutorImpl.uploadProfileImage(selectedUri,loginUser,isChange)
    }

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
    동작
View를 통해 사용자 action을 받는다.
View는 View Model로 action을 전달한다.
View Model에서 비지니스 로직을 수행한다.
View Model은 Model에게 데이터를 요청한다.
Model은 View Model의 요청에 응답한다.
View Model은 응답받은 데이터를 가공하고 저장한다.
View Model은 Data Binding을 통해 View를 수정한다

    */


}