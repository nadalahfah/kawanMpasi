package com.nadaa.mpasiapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    babyViewModel: BabyViewModel = BabyViewModel(),
    currentRoute: String = "profile",
    onNavigate: (String) -> Unit = {}
) {
    val babyData by babyViewModel.babyState.collectAsState()

    // State Form Profile
    var nameState by remember(babyData.name) { mutableStateOf(babyData.name) }
    var birthDateState by remember(babyData.birthDate) { mutableStateOf(babyData.birthDate) }

    // Dapatkan tanggal HARI INI secara otomatis (Biar umur selalu bertambah setiap hari)
    val todayStr = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)
    }

    // Dialog Modal "+ Tambah Data"
    var activeAddType by remember { mutableStateOf<MeasurementType?>(null) }
    var inputVal by remember { mutableStateOf("") }

    // Default tanggal update otomatis ke HARI INI
    var inputDate by remember {
        mutableStateOf(SimpleDateFormat("d MMM yyyy", Locale("id", "ID")).format(Calendar.getInstance().time))
    }

    // Dialog Konfirmasi Reset Data
    var showResetDialog by remember { mutableStateOf(false) }

    // Tab Filter Riwayat Pengukuran
    var selectedTab by remember { mutableStateOf(0) }

    // Modal Tambah Data
    if (activeAddType != null) {
        val titleText = when (activeAddType) {
            MeasurementType.WEIGHT -> "Tambah Berat Badan"
            MeasurementType.HEIGHT -> "Tambah Tinggi Badan"
            MeasurementType.HEAD_CIRCUMFERENCE -> "Tambah Lingkar Kepala"
            else -> ""
        }
        val unitText = when (activeAddType) {
            MeasurementType.WEIGHT -> "kg"
            MeasurementType.HEIGHT -> "cm"
            MeasurementType.HEAD_CIRCUMFERENCE -> "cm"
            else -> ""
        }

        AlertDialog(
            onDismissRequest = { activeAddType = null },
            title = { Text(titleText, fontWeight = FontWeight.Bold, color = DarkText) },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputVal,
                        onValueChange = { inputVal = it },
                        label = { Text("Nilai ($unitText)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = inputDate,
                        onValueChange = { inputDate = it },
                        label = { Text("Tanggal Update") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = inputVal.toFloatOrNull()
                        if (num != null && activeAddType != null) {
                            babyViewModel.addMeasurement(activeAddType!!, num, inputDate)
                        }
                        activeAddType = null
                        inputVal = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BottomNavBgColor)
                ) {
                    Text("Simpan", color = DarkText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeAddType = null }) {
                    Text("Batal", color = SoftText)
                }
            }
        )
    }

    // Modal Dialog Reset Data
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("⚠️ Reset Data Aplikasi?", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F)) },
            text = {
                Text("Semua riwayat pengukuran dan data profil akan dihapus bersih. Fitur ini digunakan jika aplikasi ingin dipakai dari awal untuk anak selanjutnya.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        babyViewModel.resetAllData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Ya, Reset Semua Data", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal", color = DarkText)
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Update Profil & Tumbuh Kembang",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // --- 1. CARD EDIT FOTO, NAMA & TANGGAL LAHIR ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8C8A2))
                            .clickable { /* Logika Ubah Foto */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah Foto",
                            tint = Color(0xFF5D4037),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Ubah Foto", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DarkText)
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Nama Si Kecil", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = nameState,
                            onValueChange = { nameState = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WhiteCardBg, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Tanggal Lahir (Format: YYYY-MM-DD)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = birthDateState,
                            onValueChange = { birthDateState = it },
                            placeholder = { Text("Contoh: 2024-11-10") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WhiteCardBg, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // HITUNG UMUR SEKARANG SECARA OTOMATIS BERDASARKAN TANGGAL HARI INI
                        val currentAgeCalculated = calculateAgeDetail(birthDateState, todayStr)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "👶 Umur Si Kecil Saat Ini: $currentAgeCalculated",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. KOTAK 3 RINGKASAN PENGUKURAN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MeasurementSummaryBox(
                    modifier = Modifier.weight(1f),
                    bgColor = Color(0xFFF3E8FF),
                    icon = "⚖️",
                    title = "Berat Badan",
                    value = "${babyData.weightKg} kg",
                    date = babyData.lastUpdateDate,
                    buttonBg = Color(0xFFE9D5FF),
                    onAddClick = { activeAddType = MeasurementType.WEIGHT }
                )

                MeasurementSummaryBox(
                    modifier = Modifier.weight(1f),
                    bgColor = Color(0xFFE0F2FE),
                    icon = "📏",
                    title = "Tinggi Badan",
                    value = "${babyData.heightCm} cm",
                    date = babyData.lastUpdateDate,
                    buttonBg = Color(0xFFBAE6FD),
                    onAddClick = { activeAddType = MeasurementType.HEIGHT }
                )

                MeasurementSummaryBox(
                    modifier = Modifier.weight(1f),
                    bgColor = Color(0xFFDCFCE7),
                    icon = "🗣️",
                    title = "Lingkar Kepala",
                    value = "${babyData.headCircumferenceCm} cm",
                    date = babyData.lastUpdateDate,
                    buttonBg = Color(0xFFBBF7D0),
                    onAddClick = { activeAddType = MeasurementType.HEAD_CIRCUMFERENCE }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. CARD RIWAYAT PENGUKURAN ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Riwayat Pengukuran",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(4.dp)
                    ) {
                        val tabs = listOf("Berat Badan", "Tinggi Badan", "Lingkar Kepala")
                        tabs.forEachIndexed { index, title ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (selectedTab == index) Color(0xFFE9D5FF) else Color.Transparent)
                                    .clickable { selectedTab = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = DarkText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val targetType = when (selectedTab) {
                        0 -> MeasurementType.WEIGHT
                        1 -> MeasurementType.HEIGHT
                        else -> MeasurementType.HEAD_CIRCUMFERENCE
                    }

                    val filteredRecords = babyData.records.filter { it.type == targetType }

                    if (filteredRecords.isEmpty()) {
                        Text(
                            text = "Belum ada riwayat pengukuran.",
                            fontSize = 12.sp,
                            color = SoftText,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filteredRecords.forEachIndexed { index, record ->
                                val prevValue = filteredRecords.getOrNull(index + 1)?.value
                                val diff = if (prevValue != null) record.value - prevValue else 0f
                                val unit = if (selectedTab == 0) "kg" else "cm"

                                HistoryRowItem(
                                    record = record,
                                    birthDateStr = birthDateState.ifEmpty { babyData.birthDate },
                                    diff = diff,
                                    unit = unit,
                                    onDeleteClick = { babyViewModel.deleteRecord(record.id) }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 4. BUTTON SIMPAN PERUBAHAN ---
            Button(
                onClick = {
                    babyViewModel.updateProfile(nameState, birthDateState)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowCardBg)
            ) {
                Text(
                    text = "SIMPAN PERUBAHAN",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- 5. BUTTON RESET DATA ---
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Text(
                    text = "RESET 🔄",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MeasurementSummaryBox(
    modifier: Modifier = Modifier,
    bgColor: Color,
    icon: String,
    title: String,
    value: String,
    date: String,
    buttonBg: Color,
    onAddClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = DarkText)
            Text(date, fontSize = 10.sp, color = SoftText)

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(buttonBg)
                    .clickable { onAddClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("+ Tambah Data", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DarkText)
            }
        }
    }
}

@Composable
fun HistoryRowItem(
    record: MeasurementRecord,
    birthDateStr: String,
    diff: Float,
    unit: String,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(record.date, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(
                text = calculateAgeDetail(birthDateStr, record.date),
                fontSize = 11.sp,
                color = SoftText
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${record.value} $unit",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(end = 6.dp)
            )

            val diffText = if (diff >= 0) "+%.1f $unit".format(diff) else "%.1f $unit".format(diff)
            Surface(
                color = if (diff >= 0) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = diffText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (diff >= 0) Color(0xFF15803D) else Color(0xFFB91C1C),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Riwayat",
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// FUNGSI HITUNG BEDA BULAN PINTAR (BISA HANDLE SEGALA FORMAT TANGGAL)
fun calculateAgeDetail(birthDateStr: String, recordDateStr: String): String {
    if (birthDateStr.isBlank() || recordDateStr.isBlank()) return "0 Bulan"

    return try {
        // Map konversi nama bulan Indonesia
        val monthMap = mapOf(
            "jan" to 1, "feb" to 2, "mar" to 3, "apr" to 4,
            "mei" to 5, "jun" to 6, "jul" to 7, "agu" to 8, "ags" to 8,
            "sep" to 9, "okt" to 10, "nov" to 11, "des" to 12
        )

        // 1. Parse Tanggal Lahir (Format: YYYY-MM-DD)
        val birthParts = birthDateStr.trim().split("-")
        if (birthParts.size < 3) return "0 Bulan"

        val birthYear = birthParts[0].toIntOrNull() ?: return "0 Bulan"
        val birthMonth = birthParts[1].toIntOrNull() ?: return "0 Bulan"
        val birthDay = birthParts[2].toIntOrNull() ?: 1

        // 2. Parse Tanggal Riwayat (Format: "18 Mar 2025" atau "2025-03-18")
        var recYear = 0
        var recMonth = 0
        var recDay = 1

        val recParts = recordDateStr.trim().split(" ", "-")
        if (recParts.size >= 3) {
            if (recParts[0].length == 4) {
                // Format YYYY-MM-DD
                recYear = recParts[0].toIntOrNull() ?: 0
                recMonth = recParts[1].toIntOrNull() ?: 0
                recDay = recParts[2].toIntOrNull() ?: 1
            } else {
                // Format "18 Mar 2025"
                recDay = recParts[0].toIntOrNull() ?: 1
                val monthStr = recParts[1].lowercase()
                recMonth = monthMap[monthStr] ?: recParts[1].toIntOrNull() ?: 0
                recYear = recParts[2].toIntOrNull() ?: 0
            }
        }

        if (recYear == 0 || recMonth == 0) return "0 Bulan"

        // 3. Hitung Selisih Bulan
        val calBirth = Calendar.getInstance().apply { set(birthYear, birthMonth - 1, birthDay) }
        val calRecord = Calendar.getInstance().apply { set(recYear, recMonth - 1, recDay) }

        val yearDiff = calRecord.get(Calendar.YEAR) - calBirth.get(Calendar.YEAR)
        val monthDiff = calRecord.get(Calendar.MONTH) - calBirth.get(Calendar.MONTH)

        var totalMonths = (yearDiff * 12) + monthDiff
        if (calRecord.get(Calendar.DAY_OF_MONTH) < calBirth.get(Calendar.DAY_OF_MONTH)) {
            totalMonths--
        }

        if (totalMonths <= 0) "0 Bulan" else "$totalMonths Bulan"
    } catch (e: Exception) {
        "0 Bulan"
    }
}