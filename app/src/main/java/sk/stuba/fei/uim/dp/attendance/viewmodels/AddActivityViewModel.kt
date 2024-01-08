package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.PreferenceData

class AddActivityViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _addActivityResult = MutableLiveData<String>()
    val addActivityResult: LiveData<String> get() = _addActivityResult

    val name = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val date = MutableLiveData<String>()

    fun addActivity(uid: Number){
        viewModelScope.launch {
            val result = dataRepository.apiAddActivity(
                uid,
                name.value ?: "",
                location.value ?: "",
                date.value ?: "",
                time.value ?: "",
                0
            )
            _addActivityResult.postValue(result)
        }
    }
}