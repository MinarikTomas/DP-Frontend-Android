package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.User

class AuthViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _signupResult = MutableLiveData<String>()
    val signupResult: LiveData<String> get() = _signupResult

    private val _loginResult = MutableLiveData<String>()
    val loginResult: LiveData<String> get() = _loginResult

    private val _userResult = MutableLiveData<User?>()
    val userResult: LiveData<User?> get() = _userResult

    val fullName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val repeatPassword = MutableLiveData<String>()
    val cardName = MutableLiveData<String>()

    fun loginUser() {
        viewModelScope.launch {
            val result = dataRepository.apiLoginUser(
                email.value ?: "",
                password.value ?: ""
            )
            _loginResult.postValue(result.first ?: "")
            _userResult.postValue(result.second)
        }
    }

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
                _signupResult.postValue(result.first ?: "")
                _userResult.postValue(result.second)
            }
        }
    }
}