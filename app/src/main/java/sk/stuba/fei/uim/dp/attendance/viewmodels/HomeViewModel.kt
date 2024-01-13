package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.Activity

class HomeViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _activities = MutableLiveData<List<Activity>?>()
    val activities: LiveData<List<Activity>?> get() = _activities
    private val _getActivitiesResult = MutableLiveData<String>()
    val getActivitiesResult: LiveData<String> get() = _getActivitiesResult

    fun getCreatedActivities(uid: Number){
        viewModelScope.launch {
            val result = dataRepository.apiGetCreatedActivities(uid)
            _getActivitiesResult.postValue(result.first ?: "")
            _activities.postValue(result.second)
        }
    }
}