package com.example.mobile.data.model

data class MedicineRequest(
    val name: String,
    val quantity: Int,
    val expirationDate: String,
    val supplier: String,
    val price: Double,
    val storageRoomId: String,
    val temperatureRange: TemperatureRange,
    val humidityRange: HumidityRange,
    val storageInstructions: String?
)

data class MedicineResponse(
    val _id: String,
    val name: String,
    val quantity: Int,
    val expirationDate: String,
    val supplier: String,
    val price: Double,
    val storageRoomId: StorageRoom,
    val temperatureRange: TemperatureRange,
    val humidityRange: HumidityRange,
    val storageInstructions: String?,
    val isExpiringSoon: Boolean,
    val storageConditions: StorageConditions,
    val storageStatus: String
)

data class StorageRoom(
    val _id: String,
    val name: String,
    val temperature: Double,
    val humidity: Double
)

data class TemperatureRange(
    val min: Double,
    val max: Double
)

data class HumidityRange(
    val min: Double,
    val max: Double
)

data class StorageConditions(
    val isTempValid: Boolean,
    val isHumidityValid: Boolean
)

data class EditQuantityRequest(
    val quantity: Int
)

data class SaleResponse(
    val message: String,
    val medicine: String,
    val sold: Int,
    val remaining: Int,
    val price: Double,
    val total: Double,
    val isExpiringSoon: Boolean
)

data class MedicineStats(
    val totalMedicines: Int,
    val lowStock: Int,
    val criticalStock: Int,
    val expiringSoon: Int,
    val todaySales: Int,
    val todayRevenue: Double,
    val salesData: List<SalesData>,
    val invalidStorageConditions: Int,
    val validStorageConditions: Int,
    val criticalTemperature: Int,
    val criticalHumidity: Int
)

data class SalesData(
    val date: String,
    val sales: Int,
    val revenue: Double
)
data class StorageIssuesResponse(
    val count: Int,
    val medicines: List<MedicineStorageStatus>
)

data class MedicineStorageStatus(
    val id: String,
    val name: String,
    val storageRoom: String,
    val currentTemperature: Double,
    val currentHumidity: Double,
    val temperatureRange: TemperatureRange,
    val humidityRange: HumidityRange,
    val storageStatus: String,
    val isExpiringSoon: Boolean

)
data class Range(
    val min: Double,
    val max: Double
)
