package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.data.model.Card

class ProfileViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _getCardsResult = MutableLiveData<String>()
    val getCardsResult: LiveData<String> get() = _getCardsResult
    private val _cards = MutableLiveData<List<Card>?>()
    val cards: LiveData<List<Card>?> get() = _cards

    private val _deactivateResult = MutableLiveData<String>()
    val deactivateResult: LiveData<String> get() = _deactivateResult

    private val _addCardResult = MutableLiveData<String>()
    val addCardResult: LiveData<String> get() = _addCardResult

    private val _updateCardResult = MutableLiveData<String>()
    val updateCardResult: LiveData<String> get() = _updateCardResult

    val fullName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val cardName = MutableLiveData<String>()

    fun getCards(uid: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetCards(uid)
            _getCardsResult.postValue(result.first ?: "")
            _cards.postValue(result.second)
        }
    }

    fun deactivateCard(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiDeactivateCard(id)
            _deactivateResult.postValue(result)
            val resultGet = dataRepository.apiGetCards(id)
            _cards.postValue(resultGet.second)
        }
    }

    fun addCard(id: Int, serialNumber: String){
        viewModelScope.launch {
            val resultAdd = dataRepository.apiAddCard(
                id,
                cardName.value ?: "",
                serialNumber)
            _addCardResult.postValue(resultAdd)
            val resultGet = dataRepository.apiGetCards(id)
            _cards.postValue(resultGet.second)
        }
    }

    fun updateCard(id: Int, name: String){
        viewModelScope.launch {
            val result = dataRepository.apiUpdateCard(id, name)
            _updateCardResult.postValue(result)
            val resultGet = dataRepository.apiGetCards(id)
            _cards.postValue(resultGet.second)
        }
    }
}