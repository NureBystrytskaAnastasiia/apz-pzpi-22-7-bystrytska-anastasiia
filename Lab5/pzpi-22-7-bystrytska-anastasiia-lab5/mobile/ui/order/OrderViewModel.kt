package com.example.mobile.ui.order

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.api.RetrofitInstance
import com.example.mobile.data.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _invoiceData = MutableStateFlow<String?>(null)
    val invoiceData: StateFlow<String?> = _invoiceData.asStateFlow()

    private val _pdfUri = MutableStateFlow<Uri?>(null)
    val pdfUri: StateFlow<Uri?> = _pdfUri.asStateFlow()

    fun getAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.orderApi.getAllOrders()
                if (response.isSuccessful) {
                    _orders.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Не вдалося завантажити замовлення: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Помилка мережі: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getOrderById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Спершу пробуємо найти замовлення в локальному списку
                val localOrder = _orders.value.find { it._id == id }
                if (localOrder != null) {
                    _selectedOrder.value = localOrder
                } else {
                    // Якщо не знайшли - завантажуємо з сервера
                    val response = RetrofitInstance.orderApi.getAllOrders()
                    if (response.isSuccessful) {
                        val order = response.body()?.find { it._id == id }
                        _selectedOrder.value = order
                        if (order == null) {
                            _errorMessage.value = "Замовлення не знайдено"
                        }
                    } else {
                        _errorMessage.value = "Помилка завантаження: ${response.errorBody()?.string()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Помилка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeOrder(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.orderApi.completeOrder(id)
                if (response.isSuccessful) {
                    getAllOrders() // Оновити список
                    getOrderById(id) // Оновити поточне замовлення
                } else {
                    _errorMessage.value = "Не вдалося завершити замовлення: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Помилка мережі: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearPdfUri() {
        _pdfUri.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}