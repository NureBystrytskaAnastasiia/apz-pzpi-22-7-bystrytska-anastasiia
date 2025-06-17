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
                Log.e("MedicineViewModel", "Error loading medicines", e)
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
                    val errorBody = response.errorBody()?.string()
                    Log.e("MedicineViewModel", "Failed to load stats: ${response.code()} - $errorBody")
                    _errorMessage.value = "Failed to load stats: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "Error loading stats", e)
                _errorMessage.value = "Network error: ${e.message}"
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
                Log.e("MedicineViewModel", "Error loading medicine by ID", e)
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
                Log.e("MedicineViewModel", "Error creating medicine", e)
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
                Log.e("MedicineViewModel", "Error updating medicine", e)
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
                Log.e("MedicineViewModel", "Error deleting medicine", e)
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
                Log.e("MedicineViewModel", "Error updating quantity", e)
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
                Log.e("MedicineViewModel", "Error simulating sale", e)
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
                    val roomsResponse = response.body()
                    if (roomsResponse != null) {
                        _rooms.value = roomsResponse.rooms
                        Log.d("MedicineViewModel", "Loaded ${roomsResponse.rooms.size} rooms")
                    } else {
                        Log.w("MedicineViewModel", "Rooms response is null")
                        _rooms.value = emptyList()
                        _errorMessage.value = "Empty rooms response"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MedicineViewModel", "Failed to load rooms: ${response.code()} - $errorBody")
                    _errorMessage.value = "Failed to load rooms: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "Error loading rooms", e)
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
                    val storageResponse = response.body()
                    if (storageResponse != null) {
                        _storageIssues.value = storageResponse.medicines
                        Log.d("MedicineViewModel", "Loaded ${storageResponse.medicines.size} storage issues")
                    } else {
                        Log.w("MedicineViewModel", "Storage issues response is null")
                        _storageIssues.value = emptyList()
                        _errorMessage.value = "Empty storage issues response"
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MedicineViewModel", "Failed to load storage issues: ${response.code()} - $errorBody")
                    _errorMessage.value = "Failed to load storage issues: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("MedicineViewModel", "Error loading storage issues", e)
                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    // Метод для безпечного завантаження всіх даних
    fun loadAllData() {
        viewModelScope.launch {
            try {
                // Завантажуємо основні дані
                getAllMedicines()

                // Завантажуємо статистику, але не блокуємо інші дані при помилці
                try {
                    getMedicineStats()
                } catch (e: Exception) {
                    Log.w("MedicineViewModel", "Failed to load stats, continuing with other data", e)
                }

                // Завантажуємо проблеми зберігання, але не блокуємо інші дані при помилці
                try {
                    getMedicinesWithStorageIssues()
                } catch (e: Exception) {
                    Log.w("MedicineViewModel", "Failed to load storage issues, continuing with other data", e)
                }

                // Завантажуємо кімнати
                try {
                    getAllRooms()
                } catch (e: Exception) {
                    Log.w("MedicineViewModel", "Failed to load rooms, continuing with other data", e)
                }

            } catch (e: Exception) {
                Log.e("MedicineViewModel", "Error in loadAllData", e)
                _errorMessage.value = "Failed to load data: ${e.message}"
            }
        }
    }
}
