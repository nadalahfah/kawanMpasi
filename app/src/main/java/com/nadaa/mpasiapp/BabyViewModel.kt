package com.nadaa.mpasiapp

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.*

enum class MeasurementType {
    WEIGHT, HEIGHT, HEAD_CIRCUMFERENCE
}

data class MeasurementRecord(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val type: MeasurementType,
    val value: Float
)

data class BabyState(
    val name: String = "Siti Aisyah",
    val birthDate: String = "2025-11-10",
    val unreadNotificationCount: Int = 3,
    val records: List<MeasurementRecord> = listOf(
        MeasurementRecord(date = "18 Mar 2025", type = MeasurementType.WEIGHT, value = 6.2f),
        MeasurementRecord(date = "20 Apr 2025", type = MeasurementType.WEIGHT, value = 6.6f),
        MeasurementRecord(date = "18 Mei 2025", type = MeasurementType.WEIGHT, value = 7.1f),
        MeasurementRecord(date = "20 Jun 2025", type = MeasurementType.WEIGHT, value = 7.5f),
        MeasurementRecord(date = "18 Jul 2025", type = MeasurementType.WEIGHT, value = 7.8f),
        MeasurementRecord(date = "18 Mar 2025", type = MeasurementType.HEIGHT, value = 60.0f),
        MeasurementRecord(date = "20 Apr 2025", type = MeasurementType.HEIGHT, value = 62.5f),
        MeasurementRecord(date = "18 Mei 2025", type = MeasurementType.HEIGHT, value = 65.0f),
        MeasurementRecord(date = "20 Jun 2025", type = MeasurementType.HEIGHT, value = 67.2f),
        MeasurementRecord(date = "18 Jul 2025", type = MeasurementType.HEIGHT, value = 69.5f),
        MeasurementRecord(date = "20 Jun 2025", type = MeasurementType.HEAD_CIRCUMFERENCE, value = 43.2f),
        MeasurementRecord(date = "18 Jul 2025", type = MeasurementType.HEAD_CIRCUMFERENCE, value = 44.0f)
    ),
    // DATA JADWAL MASTER
    val schedules: List<ScheduleItem> = listOf(
        ScheduleItem("1", "22 Jul 2025", "08.00", "MPASI Pagi", "Bubur Ayam Wortel", "1. Rebus dada ayam & wortel hingga lunak.\n2. Blender bersama nasi tim.\n3. Tambahkan 1 sdt minyak kelapa.", "🥣", Color(0xFFFFE4E6), isDone = true),
        ScheduleItem("2", "22 Jul 2025", "10.00", "Vitamin D", "1 Tetes", "Teteskan langsung 1 tetes ke mulut si kecil.", "🍼", Color(0xFFFEF3C7), isDone = true),
        ScheduleItem("3", "22 Jul 2025", "12.00", "Snack Siang", "Buah Alpukat", "1. Kerok buah alpukat matang.\n2. Lumatkan dengan sendok sampai halus.", "🥑", Color(0xFFE0F2FE), isDone = false),
        ScheduleItem("4", "22 Jul 2025", "17.00", "MPASI Malam", "Sup Tahu Brokoli", "1. Kukus tahu halus & brokoli.\n2. Saring halus dan sajikan selagi hangat.", "🍲", Color(0xFFF3E8FF), isDone = false),
        ScheduleItem("5", "23 Jul 2025", "08.00", "MPASI Pagi", "Purée Hati Ayam", "1. Tumis hati ayam dengan mentega.\n2. Haluskan dengan saringan.", "🥣", Color(0xFFFFE4E6), isDone = false)
    )
) {
    val weightKg: Float
        get() = records.filter { it.type == MeasurementType.WEIGHT }.lastOrNull()?.value ?: 0f

    val heightCm: Float
        get() = records.filter { it.type == MeasurementType.HEIGHT }.lastOrNull()?.value ?: 0f

    val headCircumferenceCm: Float
        get() = records.filter { it.type == MeasurementType.HEAD_CIRCUMFERENCE }.lastOrNull()?.value ?: 0f

    val lastUpdateDate: String
        get() = records.lastOrNull()?.date ?: "-"

    val weightHistory: List<Float>
        get() {
            val list = records.filter { it.type == MeasurementType.WEIGHT }.map { it.value }
            return if (list.isEmpty()) listOf(0f) else list.takeLast(6)
        }

    val heightHistory: List<Float>
        get() {
            val list = records.filter { it.type == MeasurementType.HEIGHT }.map { it.value }
            return if (list.isEmpty()) listOf(0f) else list.takeLast(6)
        }

    val ageMonth: Int
        get() = calculateAgeMonths(birthDate)

    val ageDetailString: String
        get() = calculateAgeDetail(birthDate)
}

fun calculateAgeMonths(birthDateStr: String): Int {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = sdf.parse(birthDateStr) ?: return 8
        val now = Calendar.getInstance()
        val dob = Calendar.getInstance().apply { time = birthDate }

        var months = (now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)) * 12 +
                (now.get(Calendar.MONTH) - dob.get(Calendar.MONTH))
        if (now.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            months--
        }
        if (months < 0) 0 else months
    } catch (e: Exception) {
        8
    }
}

fun calculateAgeDetail(birthDateStr: String, targetDateStr: String? = null): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = sdf.parse(birthDateStr) ?: return "8 bln 12 hr"
        val targetDate = if (targetDateStr != null) {
            val sdfDisplay = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
            sdfDisplay.parse(targetDateStr) ?: Date()
        } else Date()

        val dob = Calendar.getInstance().apply { time = birthDate }
        val target = Calendar.getInstance().apply { time = targetDate }

        var months = (target.get(Calendar.YEAR) - dob.get(Calendar.YEAR)) * 12 +
                (target.get(Calendar.MONTH) - dob.get(Calendar.MONTH))
        var days = target.get(Calendar.DAY_OF_MONTH) - dob.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val prevMonth = (target.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) return "0 bln 0 hr"
        "$months bln $days hr"
    } catch (e: Exception) {
        "8 bln 12 hr"
    }
}

class BabyViewModel : ViewModel() {
    private val _babyState = MutableStateFlow(BabyState())
    val babyState: StateFlow<BabyState> = _babyState.asStateFlow()

    fun updateProfile(name: String, birthDate: String) {
        _babyState.update { current ->
            current.copy(name = name, birthDate = birthDate)
        }
    }

    fun addMeasurement(type: MeasurementType, value: Float, dateStr: String) {
        _babyState.update { current ->
            val newRecord = MeasurementRecord(date = dateStr, type = type, value = value)
            current.copy(records = current.records + newRecord)
        }
    }

    fun deleteRecord(recordId: String) {
        _babyState.update { current ->
            current.copy(records = current.records.filterNot { it.id == recordId })
        }
    }

    // FUNGSI TAMBAH DAN UPDATE JADWAL MPASI
    fun addSchedule(item: ScheduleItem) {
        _babyState.update { current ->
            current.copy(schedules = current.schedules + item)
        }
    }

    fun toggleScheduleDone(id: String) {
        _babyState.update { current ->
            val updated = current.schedules.map {
                if (it.id == id) it.copy(isDone = !it.isDone) else it
            }
            current.copy(schedules = updated)
        }
    }

    fun clearNotifications() {
        _babyState.update { it.copy(unreadNotificationCount = 0) }
    }

    fun resetAllData() {
        _babyState.value = BabyState(
            name = "Nama Bayi",
            birthDate = "2026-01-01",
            unreadNotificationCount = 0,
            records = emptyList(),
            schedules = emptyList()
        )
    }
    // Tambahkan fungsi ini di dalam class BabyViewModel
    fun deleteSchedule(id: String) {
        _babyState.update { current ->
            current.copy(schedules = current.schedules.filterNot { it.id == id })
        }
    }
}
