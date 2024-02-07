package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.User
import sk.stuba.fei.uim.dp.attendance.utils.Event

class LoginViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _loginResult = MutableLiveData<Event<String>>()
    val loginResult: LiveData<Event<String>> get() = _loginResult

    private val _userResult = MutableLiveData<Event<User?>>()
    val userResult: LiveData<Event<User?>> get() = _userResult

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(
                email.value ?: "",
                password.value ?: ""
            )
            _loginResult.postValue(Event(result.first))
            _userResult.postValue(Event(result.second))
        }
    }

    fun googleLogin(token: String){
        viewModelScope.launch {
            val result = dataRepository.apiGoogleLogin(token)
            _loginResult.postValue(Event(result.first))
            _userResult.postValue(Event(result.second))
        }
    }
}