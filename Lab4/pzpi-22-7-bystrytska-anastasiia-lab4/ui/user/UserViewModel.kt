package com.example.mobile.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.api.RetrofitInstance
import com.example.mobile.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isDeleting = MutableStateFlow<String?>(null)
    val isDeleting: StateFlow<String?> = _isDeleting.asStateFlow()

    fun fetchUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.userApi.getAllUsers()
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Помилка завантаження користувачів: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Помилка мережі: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isDeleting.value = userId
            try {
                val response = RetrofitInstance.userApi.deleteUser(userId)
                if (response.isSuccessful) {
                    // Оновлюємо список після видалення
                    _users.value = _users.value.filter { it._id != userId }
                } else {
                    _errorMessage.value = "Помилка видалення користувача: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Помилка мережі: ${e.message}"
                e.printStackTrace()
            } finally {
                _isDeleting.value = null
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
