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
    private val _getActivityResult = MutableLiveData<Event<String>>()
    val getActivityResult: LiveData<Event<String>> get() = _getActivityResult

    private val _activityResult = MutableLiveData<Event<Activity?>>()
    val activityResult: LiveData<Event<Activity?>> get() = _activityResult

    private val _startActivityResult = MutableLiveData<Event<String>>()
    val startActivityResult: LiveData<Event<String>> get() = _startActivityResult

    private val _endActivityResult = MutableLiveData<Event<String>>()
    val endActivityResult: LiveData<Event<String>> get() = _endActivityResult

    private val _addParticipantResult = MutableLiveData<Event<String>>()
    val addParticipantResult: LiveData<Event<String>> get() = _addParticipantResult

    private val _participantResult = MutableLiveData<Event<User?>>()
    val participantResult: LiveData<Event<User?>> get() = _participantResult

    private val _activities = MutableLiveData<Event<List<Activity>?>>()
    val activities: LiveData<Event<List<Activity>?>> get() = _activities
    private val _getActivitiesResult = MutableLiveData<Event<String>>()
    val getActivitiesResult: LiveData<Event<String>> get() = _getActivitiesResult

    private val _addActivityResult = MutableLiveData<Event<String>>()
    val addActivityResult: LiveData<Event<String>> get() = _addActivityResult

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
            _getActivityResult.postValue(Event(result.first))
            _activityResult.postValue(Event(result.second))
            if(result.second != null){
                name.postValue(result.second!!.name)
                location.postValue(result.second!!.location ?: "")
                date.postValue(result.second!!.date ?: "")
                time.postValue(result.second!!.time ?: "")
            }
        }
    }

    fun startActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiStartActivity(id)
            _startActivityResult.postValue(Event(result))
        }
    }

    fun endActivity(activityId: Int, uid: Int){
        viewModelScope.launch {
            val resultEndActivity = dataRepository.apiEndActivity(activityId)
            _endActivityResult.postValue(Event(resultEndActivity))
            val resultGetActivities = dataRepository.apiGetCreatedActivities(uid)
            _activities.postValue(Event(resultGetActivities.second))
        }
    }

    fun addParticipant(id: Int, serialNumber: String){
        viewModelScope.launch {
            val result = dataRepository.apiAddParticipant(id, serialNumber)
            _addParticipantResult.postValue(Event(result.first))
            _participantResult.postValue(Event(result.second))
        }
    }

    fun getCreatedActivities(uid: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetCreatedActivities(uid)
            _getActivitiesResult.postValue(Event(result.first))
            _activities.postValue(Event(result.second))
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
            _addActivityResult.postValue(Event(addResult))
            val getResult = dataRepository.apiGetCreatedActivities(uid)
            _activities.postValue(Event(getResult.second))
        }
    }

    fun deleteActivity(id: Int, uid: Int){
        viewModelScope.launch {
            val result = dataRepository.apiDeleteCard(id)
            _deleteActivityResult.postValue(Event(result))
            val getResult = dataRepository.apiGetCreatedActivities(uid)
            _activities.postValue(Event(getResult.second))
        }
    }

    fun clearBinds(){
        name.value = ""
        location.value = ""
        time.value = ""
        date.value = ""
    }
}