package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.ParcelableActivity
import sk.stuba.fei.uim.dp.attendance.utils.Event

class EditActivityViewModel(private val dataRepository: DataRepository) : ViewModel() {

    private val _editActivityResult = MutableLiveData<Event<String>>()
    val editActivityResult: LiveData<Event<String>> get() = _editActivityResult

    val name = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val date = MutableLiveData<String>()

    fun setData(activity: ParcelableActivity){
        name.value = activity.name
        location.value = activity.location
        time.value = activity.time
        date.value = activity.date
    }

    fun updateActivity(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiUpdateActivity(
                id,
                name.value ?: "",
                location.value ?: "",
                date.value ?: "",
                time.value ?: ""
            )
            _editActivityResult.postValue(Event(result.first))
        }
    }
}