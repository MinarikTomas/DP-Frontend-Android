package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.data.model.User
import sk.stuba.fei.uim.dp.attendance.utils.Event

class ActivityViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _getActivityResult = MutableLiveData<String>()
    val getActivityResult: LiveData<String> get() = _getActivityResult

    private val _activityResult = MutableLiveData<Event<Activity?>>()
    val activityResult: LiveData<Event<Activity?>> get() = _activityResult

    private val _startActivityResult = MutableLiveData<String>()
    val startActivityResult: LiveData<String> get() = _startActivityResult

    private val _endActivityResult = MutableLiveData<String>()
    val endActivityResult: LiveData<String> get() = _endActivityResult

    private val _addParticipantResult = MutableLiveData<String>()
    val addParticipantResult: LiveData<String> get() = _addParticipantResult

    private val _participantResult = MutableLiveData<User?>()
    val participantResult: LiveData<User?> get() = _participantResult

    private val _activities = MutableLiveData<List<Activity>?>()
    val activities: LiveData<List<Activity>?> get() = _activities
    private val _getActivitiesResult = MutableLiveData<String>()
    val getActivitiesResult: LiveData<String> get() = _getActivitiesResult

    private val _addActivityResult = MutableLiveData<String>()
    val addActivityResult: LiveData<String> get() = _addActivityResult

    private val _deleteActivityResult = MutableLiveData<Event<String>>()
    val deleteActivityResult: LiveData<Event<String>> get() = _deleteActivityResult


    val name = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val weeks = MutableLiveData<String>()

    fun getActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetActivity(id)
            _getActivityResult.postValue(result.first ?: "")
            _activityResult.postValue(Event(result.second))
            if(result.second != null){
                name.postValue(result.second!!.name)
                location.postValue(result.second!!.location ?: "")
                date.postValue(result.second!!.date ?: "")
                time.postValue(result.second!!.time ?: "")
            }
        }
    }

    fun clearActivity(){
        _participantResult.value = null
    }

    fun startActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiStartActivity(id)
            _startActivityResult.postValue(result)
        }
    }

    fun endActivity(activityId: Int, uid: Int){
        viewModelScope.launch {
            val resultEndActivity = dataRepository.apiEndActivity(activityId)
            _endActivityResult.postValue(resultEndActivity)
            val resultGetActivities = dataRepository.apiGetCreatedActivities(uid)
            _activities.postValue(resultGetActivities.second)
        }
    }

    fun addParticipant(id: Int, serialNumber: String){
        viewModelScope.launch {
            val result = dataRepository.apiAddParticipant(id, serialNumber)
            _addParticipantResult.postValue(result.first ?: "")
            _participantResult.postValue(result.second)
        }
    }

    fun getCreatedActivities(uid: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetCreatedActivities(uid)
            _getActivitiesResult.postValue(result.first ?: "")
            _activities.postValue(result.second)
        }
    }

    fun addActivity(uid: Int, repeat: Boolean){
        viewModelScope.launch {
            val addResult = dataRepository.apiAddActivity(
                uid,
                name.value ?: "",
                location.value ?: "",
                date.value ?: "",
                time.value ?: "",
                when(repeat){
                    true -> weeks.value?.toInt() ?: 0
                    false -> 0
                }
            )
            _addActivityResult.postValue(addResult)
            val getResult = dataRepository.apiGetCreatedActivities(uid)
            _activities.postValue(getResult.second)
        }
    }

    fun deleteActivity(id: Int, uid: Int){
        viewModelScope.launch {
            val result = dataRepository.apiDeleteCard(id)
            _deleteActivityResult.postValue(Event(result))
            val getResult = dataRepository.apiGetCreatedActivities(uid)
            _activities.postValue(getResult.second)
        }
    }

    fun clearBinds(){
        name.value = ""
        location.value = ""
        time.value = ""
        date.value = ""
    }
}