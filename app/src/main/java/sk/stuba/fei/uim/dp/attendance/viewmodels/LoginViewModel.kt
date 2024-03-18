package sk.stuba.fei.uim.dp.attendance.viewmodels

import android.content.Context
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

    private val _resetPasswordResult = MutableLiveData<Event<String>>()
    val resetPasswordResult: LiveData<Event<String>> get() = _resetPasswordResult

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    fun loginUser(context: Context) {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(
                email.value ?: "",
                password.value ?: "",
                context

            )
            _loginResult.postValue(Event(result.first))
            _userResult.postValue(Event(result.second))
        }
    }

    fun googleLogin(token: String, context: Context){
        viewModelScope.launch {
            val result = dataRepository.apiGoogleLogin(token, context)
            _loginResult.postValue(Event(result.first))
            _userResult.postValue(Event(result.second))
        }
    }

    fun resetPassword(){
        viewModelScope.launch {
            val result = dataRepository.apiResetPassword(email.value ?: "")
            _resetPasswordResult.postValue(Event(result))
        }
    }

    fun clear(){
        email.value = ""
        password.value = ""
    }
}