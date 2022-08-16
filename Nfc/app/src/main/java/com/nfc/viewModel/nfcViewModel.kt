package com.nfc.viewModel

import androidx.lifecycle.*
import com.nfc.data.*
import com.nfc.repository.nfcRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import retrofit2.awaitResponse
import javax.inject.Inject

@HiltViewModel
class nfcViewModel @Inject constructor(
    private val nfcRepository: nfcRepositoryImpl
) : ViewModel() {

    var isReadEvent = MutableLiveData<Boolean>(false)
    var isWriteEvent = MutableLiveData<Boolean>(false)
    var isWriteResult = MutableLiveData<Boolean>(false)

    var readRiderID = MutableLiveData<String>()
    var readSerialNumber = MutableLiveData<String>()
    var writeRiderID = MutableLiveData<String>()
    var writeSerialNumber = MutableLiveData<String>()

    var objUser = MutableLiveData<User>()
    var objRider = MutableLiveData<Rider>()
    var objRiderList = MutableLiveData<List<Rider>>()
    var objResultMessage = MutableLiveData<ResultMessage>()
    var objStatistics = MutableLiveData<List<Statistics>>()
    var objProfits = MutableLiveData<List<Profits>>()

    fun getRiderId(RiderId: String, Type: Int) = viewModelScope.launch {
        val response = nfcRepository.getRiderID(RiderId, Type).awaitResponse()
        if (response.isSuccessful) {
            withContext(Main) {
                objRider.postValue(response.body()?.get(0))
            }
        }
    }

    fun getRiderList(Search: String, Type: Int) = viewModelScope.launch {
        val response = nfcRepository.getRiderList(Search, Type).awaitResponse()
        if (response.isSuccessful) {
            withContext(Main) {
                objRiderList.value = response.body()
            }
        }
    }

    fun saveSerialNumber(RiderId: Int, UserId: Int, SerialNumber: String) = viewModelScope.launch {
        val response = nfcRepository.saveSerialNumber(RiderId, UserId, SerialNumber).awaitResponse()
        if (response.isSuccessful) {
            withContext(Main) {
                objResultMessage.value = response.body()?.get(0)
                isWriteResult.value = true
            }
        }
    }


    fun Login(Id: String, Password: String) = viewModelScope.launch {
        val response = nfcRepository.Login(Id, Password).awaitResponse()
        if (response.isSuccessful) {
            withContext(Main) {
                objUser.postValue(response.body()?.get(0))
            }
        }
    }


    fun statistics() = viewModelScope.launch {
        val response = nfcRepository.statistics().awaitResponse()
        if (response.isSuccessful) {
            withContext(Main) {
                objStatistics.postValue(response.body())
            }
        }
    }


    fun profits(date:String) = viewModelScope.launch {
        val response = nfcRepository.profits(date).awaitResponse()
        if (response.isSuccessful) {
            withContext(Main) {
                objProfits.postValue(response.body())
            }
        }
    }


}