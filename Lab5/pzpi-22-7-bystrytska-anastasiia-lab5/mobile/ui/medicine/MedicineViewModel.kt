package com.example.mobile.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile.data.api.RetrofitInstance
import com.example.mobile.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Log
import retrofit2.Response

class MedicineViewModel : ViewModel() {
    private val _medicines = MutableStateFlow<List<MedicineResponse>>(emptyList())
    val medicines: StateFlow<List<MedicineResponse>> = _medicines.asStateFlow()

    private val _medicineStats = MutableStateFlow<MedicineStats?>(null)
    val medicineStats: StateFlow<MedicineStats?> = _medicineStats.asStateFlow()

    private val _selectedMedicine = MutableStateFlow<MedicineResponse?>(null)
    val selectedMedicine: StateFlow<MedicineResponse?> = _selectedMedicine.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _rooms = MutableStateFlow<List<StorageRoom>>(emptyList())
    val rooms: StateFlow<List<StorageRoom>> = _rooms.asStateFlow()

    private val _storageIssues = MutableStateFlow<List<MedicineStorageStatus>>(emptyList())
    val storageIssues: StateFlow<List<MedicineStorageStatus>> = _storageIssues.asStateFlow()

    fun getAllMedicines() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.getAllMedicines()
                if (response.isSuccessful) {
                    _medicines.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load medicines"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicineStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.getMedicineStats()
                if (response.isSuccessful) {
                    _medicineStats.value = response.body()
                } else {
                    _errorMessage.value = "Failed to load stats: ${response.code()} - ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                Log.e("MedicineViewModel", "Error loading stats", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMedicineById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.getMedicineById(id)
                if (response.isSuccessful) {
                    _selectedMedicine.value = response.body()
                } else {
                    _errorMessage.value = "Failed to load medicine"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createMedicine(medicine: MedicineRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.createMedicine(medicine)
                if (response.isSuccessful) {
                    getAllMedicines() // Refresh list
                } else {
                    _errorMessage.value = "Failed to create medicine"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMedicine(id: String, medicine: MedicineRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.updateMedicine(id, medicine)
                if (response.isSuccessful) {
                    getAllMedicines() // Refresh list
                } else {
                    _errorMessage.value = "Failed to update medicine"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMedicine(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.deleteMedicine(id)
                if (response.isSuccessful) {
                    getAllMedicines() // Refresh list
                } else {
                    _errorMessage.value = "Failed to delete medicine"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editMedicineQuantity(id: String, quantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.editMedicineQuantity(
                    id, EditQuantityRequest(quantity)
                )
                if (response.isSuccessful) {
                    getAllMedicines() // Refresh list
                } else {
                    _errorMessage.value = "Failed to update quantity"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun simulateSale() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.simulateSale()
                if (response.isSuccessful) {
                    getAllMedicines() // Refresh list
                } else {
                    _errorMessage.value = "Failed to simulate sale"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getAllRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.getAllRooms()
                if (response.isSuccessful) {
                    _rooms.value = response.body()?.rooms ?: emptyList() // Зверніть увагу на .rooms
                } else {
                    _errorMessage.value = "Failed to load rooms: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getMedicinesWithStorageIssues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.medicineApi.getMedicinesWithStorageIssues()
                if (response.isSuccessful) {
                    _storageIssues.value = response.body()?.medicines ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load storage issues: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.message}"
                Log.e("MedicineViewModel", "Error loading storage issues", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

}