package sk.stuba.fei.uim.dp.attendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sk.stuba.fei.uim.dp.attendance.data.DataRepository
import sk.stuba.fei.uim.dp.attendance.data.model.Activity
import sk.stuba.fei.uim.dp.attendance.data.model.Card
import sk.stuba.fei.uim.dp.attendance.utils.Event

class ProfileViewModel(private val dataRepository: DataRepository): ViewModel() {
    private val _getCardsResult = MutableLiveData<Event<String>>()
    val getCardsResult: LiveData<Event<String>> get() = _getCardsResult
    private val _cards = MutableLiveData<Event<List<Card>?>>()
    val cards: LiveData<Event<List<Card>?>> get() = _cards

    private val _deactivateResult = MutableLiveData<Event<String>>()
    val deactivateResult: LiveData<Event<String>> get() = _deactivateResult

    private val _addCardResult = MutableLiveData<Event<String>>()
    val addCardResult: LiveData<Event<String>> get() = _addCardResult

    private val _updateCardResult = MutableLiveData<Event<String>>()
    val updateCardResult: LiveData<Event<String>> get() = _updateCardResult

    val fullName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val cardName = MutableLiveData<String>()

    fun getCards(uid: Int){
        viewModelScope.launch {
            val result = dataRepository.apiGetCards(uid)
            _getCardsResult.postValue(Event(result.first))
            _cards.postValue(Event(result.second))
        }
    }

    fun deactivateCard(id: Int){
        viewModelScope.launch {
            val result = dataRepository.apiDeactivateCard(id)
            _deactivateResult.postValue(Event(result))
            val resultGet = dataRepository.apiGetCards(id)
            _cards.postValue(Event(resultGet.second))
        }
    }

    fun addCard(id: Int, serialNumber: String){
        viewModelScope.launch {
            val resultAdd = dataRepository.apiAddCard(
                id,
                cardName.value ?: "",
                serialNumber)
            _addCardResult.postValue(Event(resultAdd))
            val resultGet = dataRepository.apiGetCards(id)
            _cards.postValue(Event(resultGet.second))
        }
    }

    fun updateCard(id: Int, name: String){
        viewModelScope.launch {
            val result = dataRepository.apiUpdateCard(id, name)
            _updateCardResult.postValue(Event(result))
            val resultGet = dataRepository.apiGetCards(id)
            _cards.postValue(Event(resultGet.second))
        }
    }
}