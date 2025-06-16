package com.example.mobile.ui.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.api.RetrofitInstance
import com.example.mobile.data.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.roomApi.getRooms()
                if (response.isSuccessful) {
                    val roomResponse = response.body()
                    if (roomResponse != null) {
                        _rooms.value = roomResponse.rooms
                    } else {
                        _errorMessage.value = "Порожня відповідь від сервера"
                    }
                } else {
                    _errorMessage.value = "Помилка сервера: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Помилка мережі: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}