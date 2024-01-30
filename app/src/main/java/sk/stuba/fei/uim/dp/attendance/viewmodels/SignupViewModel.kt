package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.utils.Event

class SignupViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _signupResult = MutableLiveData<Event<String>>()
    val signupResult: LiveData<Event<String>> get() = _signupResult

    val fullName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeatPassword = MutableLiveData<String>()
    val cardName = MutableLiveData<String>()

    fun signupUser(serialNumber: String){
        if (!fullName.value.isNullOrEmpty() && !email.value.isNullOrEmpty() &&
            !password.value.isNullOrEmpty() && !repeatPassword.value.isNullOrEmpty()){
            viewModelScope.launch {
                val result = dataRepository.apiSignupUser(
                    fullName.value ?: "",
                    email.value ?: "",
                    password.value ?: "",
                    cardName.value ?: "",
                    serialNumber
                )
                _signupResult.postValue(Event(result))
            }
        }
    }
}