package com.example.mobile.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobile.data.model.MedicineStats
import com.example.mobile.data.model.MedicineStorageStatus
import com.example.mobile.ui.theme.*

@Composable
fun StatsView(stats: MedicineStats?, storageIssues: List<MedicineStorageStatus>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        if (stats != null) {
            // Основні статистики
            StatItem("Загальна кількість ліків", stats.totalMedicines.toString())
            StatItem("Ліки з низьким запасом (<10)", stats.lowStock.toString())
            StatItem("Ліки з критичним запасом (<5)", stats.criticalStock.toString())
            StatItem("Ліки з терміном придатності (<7 днів)", stats.expiringSoon.toString())
            StatItem("Продажі сьогодні", stats.todaySales.toString())
            StatItem("Дохід сьогодні", "%.2f грн".format(stats.todayRevenue))

            Spacer(modifier = Modifier.height(16.dp))

            // Продажі за останні 7 днів
            if (stats.salesData.isNotEmpty()) {
                Text(
                    "Продажі за останні 7 днів:",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                stats.salesData.forEach { data ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            data.date,
                            modifier = Modifier.weight(1f),
                            color = TextPrimary
                        )
                        Text(
                            "${data.sales} продажів",
                            modifier = Modifier.weight(1f),
                            color = TextPrimary
                        )
                        Text(
                            "%.2f грн".format(data.revenue),
                            modifier = Modifier.weight(1f),
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Purple20
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Статистика недоступна",
                        style =MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        "Дані завантажуються або сталася помилка",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Статус зберігання ліків
        Text(
            "Статус зберігання ліків:",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (storageIssues.isNotEmpty()) {
            val optimalCount = storageIssues.count { it.storageStatus == "Оптимальні умови" }
            val outOfRangeCount = storageIssues.count { it.storageStatus == "Поза межами норми" }

            StatusMessage(
                message = "Ліки з оптимальними умовами: $optimalCount",
                isWarning = false
            )
            StatusMessage(
                message = "Ліки з невірними умовами: $outOfRangeCount",
                isWarning = outOfRangeCount > 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Детальний стан зберігання
            Text(
                "Детальний стан зберігання:",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            storageIssues.forEach { medicine ->
                StorageIssueItem(medicine)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Purple20
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Дані про стан зберігання недоступні",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        "Дані завантажуються або сталася помилка",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun StorageIssueItem(medicine: MedicineStorageStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (medicine.storageStatus) {
                "Оптимальні умови" -> SuccessContainer
                "Поза межами норми" -> ErrorContainer
                else -> Purple20
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                medicine.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                "Кімната: ${medicine.storageRoom}",
                color = TextSecondary
            )
            Text(
                "Статус: ${medicine.storageStatus}",
                color = when (medicine.storageStatus) {
                    "Оптимальні умови" -> Success
                    "Поза межами норми" -> Error
                    else -> TextPrimary
                }
            )

            if (medicine.isExpiringSoon) {
                Text(
                    "⚠️ Термін придатності закінчується",
                    style = MaterialTheme.typography.bodySmall,
                    color = Warning
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Температура:", color = TextSecondary)
                    Text(
                        "Поточна: ${medicine.currentTemperature}°C",
                        color = TextPrimary
                    )
                    Text(
                        "Діапазон: ${medicine.temperatureRange.min}°C - ${medicine.temperatureRange.max}°C",
                        color = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Вологість:", color = TextSecondary)
                    Text(
                        "Поточна: ${medicine.currentHumidity}%",
                        color = TextPrimary
                    )
                    Text(
                        "Діапазон: ${medicine.humidityRange.min}% - ${medicine.humidityRange.max}%",
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = TextPrimary
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge,
            color = AccentPrimary
        )
    }
}

@Composable
fun StatusMessage(message: String, isWarning: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isWarning) Icons.Filled.Warning else Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = if (isWarning) Error else Success,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = if (isWarning) Error else Success,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
