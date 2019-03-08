package com.ujujzk.tryworkmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {

    val onSelectClick = MutableLiveData<Int>()

    fun selectDictionary(){
        onSelectClick.value = 0
    }



}