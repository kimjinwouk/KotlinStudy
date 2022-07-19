package com.kimjinwouk.petwalk.viewmodel

import a.jinkim.calculate.model.Walking
import androidx.lifecycle.*
import com.kimjinwouk.petwalk.repository.WalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class walkViewModel @Inject constructor(
    private val walkRepository: WalkRepository
) : ViewModel() {


    val walks : LiveData<List<Walking>>

    init {
        walks = walkRepository.readAllData
    }

    fun insertWalk(walk: Walking) = viewModelScope.launch {
        walkRepository.insertWalk(walk)
    }

    fun selectWalk() = viewModelScope.launch {
        walkRepository.selectAll()
    }
}