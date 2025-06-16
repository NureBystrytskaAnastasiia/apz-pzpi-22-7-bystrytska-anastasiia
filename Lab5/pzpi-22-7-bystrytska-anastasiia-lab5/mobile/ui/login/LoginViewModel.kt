package com.example.mobile.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.api.RetrofitInstance
import com.example.mobile.data.model.LoginRequest
import com.example.mobile.data.model.RegisterRequest
import com.example.mobile.data.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val tokenStorage: TokenStorage) : ViewModel() {

    private val _loginResult = MutableStateFlow<String?>(null)
    val loginResult: StateFlow<String?> = _loginResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginResult.value = "Заповніть всі поля"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        tokenStorage.saveToken(loginResponse.token)
                        tokenStorage.saveRole(loginResponse.user.role)
                        _userRole.value = loginResponse.user.role
                        _loginResult.value = "Успішний вхід!"
                        _isLoggedIn.value = true
                    }
                } else {
                    // Детальніша обробка помилок
                    val errorBody = response.errorBody()?.string()
                    _loginResult.value = when {
                        errorBody?.contains("Invalid credentials") == true -> {
                            "Невірний email або пароль"
                        }
                        errorBody?.contains("User not found") == true -> {
                            "Користувача не знайдено"
                        }
                        else -> {
                            "Помилка входу: ${errorBody ?: "Невідома помилка"}"
                        }
                    }
                }
            } catch (e: Exception) {
                _loginResult.value = "Помилка мережі: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun register(email: String, password: String, name: String, role: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _loginResult.value = "Заповніть всі поля"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.register(
                    RegisterRequest(email, password, name, role) // Використовуємо передану роль
                )
                if (response.isSuccessful) {
                    _loginResult.value = "Реєстрація успішна! Тепер увійдіть"
                } else {
                    _loginResult.value = "Помилка реєстрації: ${response.errorBody()?.string() ?: "Невідома помилка"}"
                }
            } catch (e: Exception) {
                _loginResult.value = "Помилка мережі: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _loginResult.value = "Введіть email"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.forgotPassword(mapOf("email" to email))
                if (response.isSuccessful) {
                    _loginResult.value = "Новий пароль надіслано на пошту"
                } else {
                    _loginResult.value = "Користувача з такою поштою не знайдено"
                }
            } catch (e: Exception) {
                _loginResult.value = "Помилка мережі: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                RetrofitInstance.api.logout()
                tokenStorage.clearToken()
                tokenStorage.clearRole()
                _isLoggedIn.value = false
                _userRole.value = null
                _loginResult.value = "Вихід виконано"
            } catch (e: Exception) {
                _loginResult.value = "Помилка виходу: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        _loginResult.value = null
    }
}