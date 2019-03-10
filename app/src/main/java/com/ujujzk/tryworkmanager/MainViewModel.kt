package com.ujujzk.tryworkmanager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.ujujzk.tryworkmanager.workers.ParseDictionaryWorker


class MainViewModel : ViewModel() {

    val onSelectClick = MutableLiveData<Int>()
    val parseDicWork: LiveData<List<WorkInfo>>
    private val workManager: WorkManager = WorkManager.getInstance()

    fun selectDictionary(){
        onSelectClick.value = 0
    }

    init {
        parseDicWork = workManager.getWorkInfosByTagLiveData(ParseDictionaryWorker.ACCESS_TAG)

    }

    fun parseDictionary(dicPath: String) {
        workManager.enqueue(OneTimeWorkRequestBuilder<ParseDictionaryWorker>()
            .addTag(ParseDictionaryWorker.ACCESS_TAG)
            .setInputData(workDataOf(ParseDictionaryWorker.KEY_DIC_URI to dicPath))
            .build())
    }

}