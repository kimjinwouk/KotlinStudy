package com.nfc.repository


import com.nfc.data.*
import com.nfc.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Response

import javax.inject.Inject


// 앱에서 사용하는 데이터와 그 데이터 통신을 하는 역할
class nfcRepositoryImpl @Inject constructor(
    private val RetrofitService: RetrofitService
): nfcRepository {
    override suspend fun Login(Id: String, password: String): Call<List<User>> {
        return RetrofitService.ie_nfc_login(Id,password);
    }


    override suspend fun getRiderID(a : String, b : Int) : Call<List<Rider>> {
        return RetrofitService.ie_nfc_getriderid(a,b);
    }


    override suspend fun getRiderList(a : String, b : Int) : Call<List<Rider>> {
        return RetrofitService.ie_nfc_getriderid(a,b);
    }

    override suspend fun saveSerialNumber(
        RiderId: Int,
        UserId: Int,
        SerialNumber: String
    ): Call<List<ResultMessage>> {
        return RetrofitService.ie_nfc_savedata(RiderId,UserId,SerialNumber);
    }


    override suspend fun statistics(): Call<List<Statistics>> {
        return RetrofitService.ie_nfc_statistics()
    }

    override suspend fun profits(date: String): Call<List<Profits>> {
        return RetrofitService.ie_nfc_profits(date)
    }

    /*
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