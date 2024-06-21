package com.lifthub.lifthubandroid.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _activityResult = MutableLiveData<ActivityResultData>()
    val activityResult: LiveData<ActivityResultData> = _activityResult

    fun setActivityResult(resultData: ActivityResultData) {
        _activityResult.value = resultData
    }
}

data class ActivityResultData(
    val isUnsaved: Boolean,
    val routineNameResult: String?,
)