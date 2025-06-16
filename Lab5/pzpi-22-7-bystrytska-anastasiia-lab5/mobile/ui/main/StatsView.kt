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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mobile.data.model.MedicineStats
import com.example.mobile.data.model.MedicineStorageStatus

@Composable
fun StatsView(stats: MedicineStats, storageIssues: List<MedicineStorageStatus>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(scrollState) // додаємо вертикальний скрол
    ) {
        // Основні статистики
        StatItem("Загальна кількість ліків", stats.totalMedicines.toString())
        StatItem("Ліки з низьким запасом (<10)", stats.lowStock.toString())
        StatItem("Ліки з критичним запасом (<5)", stats.criticalStock.toString())
        StatItem("Ліки з терміном придатності (<7 днів)", stats.expiringSoon.toString())
        StatItem("Продажі сьогодні", stats.todaySales.toString())
        StatItem("Дохід сьогодні", "%.2f грн".format(stats.todayRevenue))

        Spacer(modifier = Modifier.height(16.dp))

        // Статус зберігання ліків
        Text("Статус зберігання ліків:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        val optimalCount = storageIssues.count { it.storageStatus == "Оптимальні умови" }
        val outOfRangeCount = storageIssues.count { it.storageStatus == "Поза межами норми" }

        StatusMessage(
            message = "Ліки з оптимальними умовами: $optimalCount",
            isWarning = false
        )
        StatusMessage(
            message = "Ліки з невірними умовами: $outOfRangeCount",
            isWarning = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Детальний стан зберігання
        Text("Детальний стан зберігання:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        storageIssues.forEach { medicine ->
            StorageIssueItem(medicine)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Продажі за останні 7 днів
        Text("Продажі за останні 7 днів:", style = MaterialTheme.typography.titleMedium)
        stats.salesData.forEach { data ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(data.date, modifier = Modifier.weight(1f))
                Text("${data.sales} продажів", modifier = Modifier.weight(1f))
                Text("%.2f грн".format(data.revenue), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StorageIssueItem(medicine: MedicineStorageStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (medicine.storageStatus == "Оптимальні умови")
                Color.Green.copy(alpha = 0.1f)
            else
                Color.Red.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(medicine.name, style = MaterialTheme.typography.titleMedium)
            Text("Кімната: ${medicine.storageRoom}")
            Text(
                "Статус: ${medicine.storageStatus}",
                color = if (medicine.storageStatus == "Оптимальні умови") Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Температура:")
                    Text("Поточна: ${medicine.currentTemperature}°C")
                    Text("Діапазон: ${medicine.temperatureRange.min}°C - ${medicine.temperatureRange.max}°C")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Вологість:")
                    Text("Поточна: ${medicine.currentHumidity}%")
                    Text("Діапазон: ${medicine.humidityRange.min}% - ${medicine.humidityRange.max}%")
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
        Text(label, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge)
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
            tint = if (isWarning) Color.Red else Color.Green,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = message,
            color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}
