package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.Activity

class AttendedActivitiesViewModel(private val dataRepository: DataRepository) : ViewModel() {
    private val _getAttendedActivitiesResult = MutableLiveData<String>()
    val getAttendedActivitiesResult: LiveData<String> get() = _getAttendedActivitiesResult
    private val _attendedActivities = MutableLiveData<List<Activity>?>()
    val attendedActivities: LiveData<List<Activity>?> get() = _attendedActivities

    fun getAttendedActivities(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetAttendedActivities(id)
            _getAttendedActivitiesResult.postValue(result.first ?: "")
            _attendedActivities.postValue(result.second)
        }
    }
}