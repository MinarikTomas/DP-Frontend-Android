package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.data.model.User

class ActivityViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _getActivityResult = MutableLiveData<String>()
    val getActivityResult: LiveData<String> get() = _getActivityResult

    private val _activityResult = MutableLiveData<Activity?>()
    val activityResult: LiveData<Activity?> get() = _activityResult

    private val _startActivityResult = MutableLiveData<String>()
    val startActivityResult: LiveData<String> get() = _startActivityResult

    private val _endActivityResult = MutableLiveData<String>()
    val endActivityResult: LiveData<String> get() = _endActivityResult

    private val _addParticipantResult = MutableLiveData<String>()
    val addParticipantResult: LiveData<String> get() = _addParticipantResult

    private val _participantResult = MutableLiveData<User?>()
    val participantResult: LiveData<User?> get() = _participantResult

    val name = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val date = MutableLiveData<String>()

    fun getActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetActivity(id)
            _getActivityResult.postValue(result.first ?: "")
            _activityResult.postValue(result.second)
            if(result.second != null){
                name.postValue(result.second!!.name)
                location.postValue(result.second!!.location ?: "")
                date.postValue(result.second!!.date ?: "")
                time.postValue(result.second!!.time ?: "")
            }
        }
    }

    fun clearActivity(){
        _activityResult.value = null
        _participantResult.value = null
    }

    fun startActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiStartActivity(id)
            _startActivityResult.postValue(result)
        }
    }

    fun endActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiEndActivity(id)
            _endActivityResult.postValue(result)
        }
    }

    fun addParticipant(id: Int, serialNumber: String){
        viewModelScope.launch {
            val result = dataRepository.apiAddParticipant(id, serialNumber)
            _addParticipantResult.postValue(result.first ?: "")
            _participantResult.postValue(result.second)
        }
    }
}