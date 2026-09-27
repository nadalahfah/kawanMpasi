package com.nadaa.mpasiapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// MODEL DATA SCHEDULE
data class ScheduleItem(
    val id: String,
    val dateStr: String,
    val time: String,
    val title: String,
    val subtitle: String,
    val recipeSteps: String = "1. Rebus ayam & wortel.\n2. Haluskan bersama bubur.\n3. Sajikan hangat.",
    val iconEmoji: String = "🥣",
    val iconBgColor: Color = Color(0xFFFFE4E6),
    var isDone: Boolean = false
)

data class CalendarDay(
    val dayName: String,
    val dayNumber: String,
    val dateFullStr: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    babyViewModel: BabyViewModel = remember { BabyViewModel() },
    currentRoute: String = "schedule",
    onNavigate: (String) -> Unit = {}
) {
    val babyState by babyViewModel.babyState.collectAsState()
    var selectedDateStr by remember { mutableStateOf("22 Jul 2025") }
    var showAddDialog by remember { mutableStateOf(false) }

    // State untuk Konfirmasi Hapus
    var itemToDelete by remember { mutableStateOf<ScheduleItem?>(null) }

    val calendarDays = remember {
        listOf(
            CalendarDay("Sen", "21", "21 Jul 2025"),
            CalendarDay("Sel", "22", "22 Jul 2025"),
            CalendarDay("Rab", "23", "23 Jul 2025"),
            CalendarDay("Kam", "24", "24 Jul 2025"),
            CalendarDay("Jum", "25", "25 Jul 2025"),
            CalendarDay("Sab", "26", "26 Jul 2025"),
            CalendarDay("Min", "27", "27 Jul 2025")
        )
    }

    // Modal Tambah Manual
    if (showAddDialog) {
        var inputTitle by remember { mutableStateOf("") }
        var inputMenu by remember { mutableStateOf("") }
        var inputTime by remember { mutableStateOf("08.00") }
        var inputRecipe by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Menu Manual ✨", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Tanggal Terpilih: $selectedDateStr", fontSize = 12.sp, color = SoftText, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("Kategori (misal: MPASI Pagi)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputMenu,
                        onValueChange = { inputMenu = it },
                        label = { Text("Nama Menu (misal: Bubur Daging)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputTime,
                        onValueChange = { inputTime = it },
                        label = { Text("Jam (misal: 08.00)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = inputRecipe,
                        onValueChange = { inputRecipe = it },
                        label = { Text("Cara Pembuatan / Resep") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputTitle.isNotBlank() && inputMenu.isNotBlank()) {
                            babyViewModel.addSchedule(
                                ScheduleItem(
                                    id = System.currentTimeMillis().toString(),
                                    dateStr = selectedDateStr,
                                    time = inputTime,
                                    title = inputTitle,
                                    subtitle = inputMenu,
                                    recipeSteps = inputRecipe.ifBlank { "Tidak ada instruksi khusus." },
                                    iconEmoji = "🍲",
                                    iconBgColor = Color(0xFFFFE4E6)
                                )
                            )
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF472B6))
                ) {
                    Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal", color = SoftText)
                }
            }
        )
    }

    // Modal Konfirmasi Hapus
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Jadwal 🗑️", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText) },
            text = { Text("Apakah kamu yakin ingin menghapus '${item.subtitle}' dari jadwal?", fontSize = 13.sp, color = SoftText) },
            confirmButton = {
                Button(
                    onClick = {
                        babyViewModel.deleteSchedule(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal", color = SoftText)
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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // --- 1. HEADER + TOMBOL TAMBAH ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Jadwal & Agenda", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(" ✨", fontSize = 20.sp)
                    }
                    Text("Jangan lewatkan momen pentingnya 💖", fontSize = 12.sp, color = SoftText)
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFBCFE8)),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah", tint = Color(0xFF9D174D), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah", color = Color(0xFF9D174D), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. KALENDER HORIZONTAL ---
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(calendarDays) { day ->
                    val isSelected = day.dateFullStr == selectedDateStr

                    Box(
                        modifier = Modifier
                            .width(55.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFFF472B6) else Color.White)
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedDateStr = day.dateFullStr }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = day.dayName,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else SoftText,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = day.dayNumber,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.White else DarkText,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Hari Ini, $selectedDateStr",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. LIST JADWAL ---
            val currentSchedule = babyState.schedules.filter { it.dateStr == selectedDateStr }

            if (currentSchedule.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada jadwal untuk tanggal ini 😊", color = SoftText, fontSize = 13.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    currentSchedule.forEach { item ->
                        ScheduleExpandableCard(
                            item = item,
                            onToggleDone = { babyViewModel.toggleScheduleDone(item.id) },
                            onDelete = { itemToDelete = item }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// ITEM CARD KARTU JADWAL DENGAN TOMBOL HAPUS
@Composable
fun ScheduleExpandableCard(
    item: ScheduleItem,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhiteCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Emoji Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(item.iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.iconEmoji, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Jam
                Text(
                    text = item.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Judul & Subjudul
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(text = item.subtitle, fontSize = 11.sp, color = SoftText)
                }

                // Tombol Panah Detail
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Detail",
                        tint = SoftText,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Tombol Ceklis
                IconButton(
                    onClick = onToggleDone,
                    modifier = Modifier.size(28.dp)
                ) {
                    if (item.isDone) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selesai",
                            tint = Color(0xFF22C55E),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFD1D5DB), CircleShape)
                        )
                    }
                }

                // --- TOMBOL HAPUS (BARU!) ---
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Jadwal",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Detail Resep ketika di-Expand
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFAFAFA))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "👩‍🍳 Cara Pembuatan / Catatan:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.recipeSteps,
                        fontSize = 12.sp,
                        color = Color(0xFF4B5563),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}