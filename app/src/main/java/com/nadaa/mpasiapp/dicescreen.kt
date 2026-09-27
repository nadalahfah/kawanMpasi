package com.nadaa.mpasiapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

data class RecipeItem(
    val id: Int,
    val title: String,
    val time: String,
    val ageGroup: String,
    val allergens: List<String>,
    val ingredients: List<String>,
    val imageRes: Int = R.drawable.baby
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(
    babyViewModel: BabyViewModel = remember { BabyViewModel() },
    currentRoute: String = "dice",
    selectedBabyAge: String = "6-8 Bulan",
    onNavigate: (String) -> Unit = {}
) {
    // --- DATA MASTER RESEP ---
    val allRecipes = remember {
        listOf(
            RecipeItem(1, "Bubur Ayam Wortel", "20 Mins", "6-8 Bulan", emptyList(), listOf("Nasi", "Ayam", "Wortel")),
            RecipeItem(2, "Puree Daging Sapi & Kentang", "25 Mins", "6-8 Bulan", listOf("Susu"), listOf("Kentang", "Daging Sapi", "UB")),
            RecipeItem(3, "Puree Alpukat Manis", "10 Mins", "6-8 Bulan", listOf("Susu"), listOf("Alpukat", "ASI/Sufor")),
            RecipeItem(4, "Bubur Hati Ayam Labu Siam", "20 Mins", "6-8 Bulan", emptyList(), listOf("Beras", "Hati Ayam", "Labu Siam")),
            RecipeItem(5, "Puree Kabocha Daging", "20 Mins", "6-8 Bulan", emptyList(), listOf("Kabocha", "Daging Sapi", "Minyak Kelapa")),
            RecipeItem(6, "Omelet Telur Sayur", "15 Mins", "9-11 Bulan", listOf("Telur"), listOf("Telur", "Bayam", "Keju")),
            RecipeItem(7, "Nasi Tim Udang", "30 Mins", "9-11 Bulan", listOf("Seafood"), listOf("Beras", "Udang", "Tahu")),
            RecipeItem(8, "Bubur Kacang Hijau Smooth", "25 Mins", "9-11 Bulan", listOf("Kacang"), listOf("Kacang Hijau", "Santan")),
            RecipeItem(9, "Nasi Tim Ayam Brokoli", "25 Mins", "9-11 Bulan", emptyList(), listOf("Beras", "Ayam Fillet", "Brokoli")),
            RecipeItem(10, "Bubur Salmon Wortel", "20 Mins", "9-11 Bulan", listOf("Seafood"), listOf("Beras", "Ikan Salmon", "Wortel")),
            RecipeItem(11, "Nasi Goreng Mentega Ayam", "20 Mins", "12+ Bulan", listOf("Susu"), listOf("Nasi", "Ayam", "Butter")),
            RecipeItem(12, "Sup Ikan Patin Jagung", "25 Mins", "12+ Bulan", listOf("Seafood"), listOf("Ikan Patin", "Jagung", "Tahu")),
            RecipeItem(13, "Bola-Bola Daging Keju", "20 Mins", "12+ Bulan", listOf("Susu", "Telur"), listOf("Daging Cincang", "Keju", "Telur")),
            RecipeItem(14, "Nasi Bakso Ayam Sayur", "25 Mins", "12+ Bulan", emptyList(), listOf("Nasi", "Bakso Ayam", "Wortel", "Buncis"))
        )
    }

    // --- STATE FITUR ---
    var activeAgeFilter by remember { mutableStateOf(selectedBabyAge) }
    var selectedAllergies by remember { mutableStateOf(setOf<String>()) }
    var diceImageRes by remember { mutableStateOf(R.drawable.dice_1) }
    var selectedRecipe by remember { mutableStateOf<RecipeItem?>(null) }
    var selectedMealTime by remember { mutableStateOf("Pagi") }
    var showInfoBottomSheet by remember { mutableStateOf(false) }

    // STATE POP-UP SIMPAN JADWAL
    var showSaveDialog by remember { mutableStateOf(false) }
    var targetDateStr by remember { mutableStateOf("22 Jul 2025") }
    var targetTime by remember { mutableStateOf("08.00") }

    val diceDrawables = listOf(
        R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3,
        R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6
    )

    val filteredRecipes = remember(activeAgeFilter, selectedAllergies) {
        allRecipes.filter { recipe ->
            val matchAge = recipe.ageGroup == activeAgeFilter
            val hasAllergen = recipe.allergens.any { it in selectedAllergies }
            matchAge && !hasAllergen
        }
    }

    LaunchedEffect(filteredRecipes) {
        if (filteredRecipes.isNotEmpty()) {
            if (selectedRecipe == null || selectedRecipe !in filteredRecipes) {
                selectedRecipe = filteredRecipes.random()
            }
        } else {
            selectedRecipe = null
        }
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
                .background(BackgroundPink)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // --- 1. HEADER + ICON INFO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Dadu MPASI", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(" ✨", fontSize = 18.sp)
                    }
                    Text("Kocok dadu untuk dapatkan ide menu hari ini!", fontSize = 12.sp, color = SoftText)
                }

                IconButton(onClick = { showInfoBottomSheet = true }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Info Cara Kerja",
                        tint = Color(0xFFF43F5E),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. DADU & TOMBOL KOCOK ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = diceImageRes),
                    contentDescription = "Dice",
                    modifier = Modifier.size(140.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val randomIdx = Random.nextInt(0, 6)
                        diceImageRes = diceDrawables[randomIdx]
                        selectedRecipe = if (filteredRecipes.isNotEmpty()) {
                            if (filteredRecipes.size > 1) {
                                filteredRecipes.filter { it.id != selectedRecipe?.id }.random()
                            } else filteredRecipes.first()
                        } else null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB7185)),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth(0.7f).height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎲  ", fontSize = 16.sp)
                        Text("Kocok Dadu", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Butuh inspirasi menu sehat untuk si Kecil?\nYuk, kocok dadu!",
                    fontSize = 11.sp,
                    color = SoftText,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 3. FILTER USIA & ALERGI ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Filter Usia", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("6-8 Bulan", "9-11 Bulan", "12+ Bulan").forEach { age ->
                            val isSelected = activeAgeFilter == age
                            Button(
                                onClick = { activeAgeFilter = age },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFFFDA4AF) else Color(0xFFFFF1F2)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(age, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) Color.White else DarkText)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Alergi / Pantangan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Spacer(modifier = Modifier.height(8.dp))

                    val allergyOptions = listOf(
                        Pair("Telur", "🍳"),
                        Pair("Susu", "🥛"),
                        Pair("Seafood", "🦐"),
                        Pair("Kacang", "🥜")
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        allergyOptions.forEach { (name, emoji) ->
                            val isChecked = selectedAllergies.contains(name)
                            Surface(
                                color = if (isChecked) Color(0xFFFFF1F2) else Color(0xFFFAFAFA),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, if (isChecked) Color(0xFFFDA4AF) else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedAllergies = if (isChecked) selectedAllergies - name else selectedAllergies + name
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(emoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(name, fontSize = 10.sp, color = DarkText)
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = null
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. HASIL RESEP ---
            if (selectedRecipe != null) {
                selectedRecipe?.let { recipe ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PurpleCardBg),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = recipe.imageRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(56.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(recipe.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                    Text("⏱️ ${recipe.time} • 👶 ${recipe.ageGroup}", fontSize = 11.sp, color = SoftText)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Bahan: ${recipe.ingredients.joinToString(", ")}",
                                fontSize = 11.sp,
                                color = SoftText
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Pilih Waktu Makan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Pagi", "Siang", "Malam").forEach { meal ->
                                    FilterChip(
                                        selected = selectedMealTime == meal,
                                        onClick = {
                                            selectedMealTime = meal
                                            targetTime = when (meal) {
                                                "Pagi" -> "08.00"
                                                "Siang" -> "12.00"
                                                else -> "17.00"
                                            }
                                        },
                                        label = { Text(meal, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFFFB7185),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showSaveDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Simpan ke Jadwal ($selectedMealTime)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = WhiteCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🚫", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada resep yang sesuai dengan filter alergi ini.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SoftText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- 5. TABEL ANJURAN & PANDUAN MPASI (SUDAH KEMBALI!) ---
            Text(
                text = "💡 Panduan & Anjuran Pemberian MPASI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WhiteCardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFB703), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text("Usia", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                        Text("Tekstur", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                        Text("Frekuensi", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                        Text("Porsi", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    MpasiguideRow("6-8 Bln", "Bubur kental / Lumat", "2-3x / hari", "2-3 sdm s/d ½ mangkok")
                    Divider(color = Color(0xFFF1F5F9))
                    MpasiguideRow("9-11 Bln", "Cincang halus / Finger food", "3-4x / hari", "½ s/d ¾ mangkok")
                    Divider(color = Color(0xFFF1F5F9))
                    MpasiguideRow("12-23 Bln", "Makanan keluarga", "3-4x / hari", "¾ s/d 1 mangkok")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // --- POP UP DIALOG SIMPAN KE TANGGAL BERAPA ---
    if (showSaveDialog && selectedRecipe != null) {
        val datesList = listOf("21 Jul 2025", "22 Jul 2025", "23 Jul 2025", "24 Jul 2025", "25 Jul 2025", "26 Jul 2025", "27 Jul 2025")

        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Simpan ke Jadwal 📅", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Menu: ${selectedRecipe?.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                    Text("Pilih Tanggal Penyajian:", fontSize = 12.sp, color = SoftText)

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        datesList.forEach { date ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (targetDateStr == date) Color(0xFFFBCFE8) else Color(0xFFFAFAFA))
                                    .clickable { targetDateStr = date }
                                    .padding(8.dp)
                            ) {
                                RadioButton(selected = targetDateStr == date, onClick = { targetDateStr = date })
                                Text(date, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = DarkText)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newItem = ScheduleItem(
                            id = System.currentTimeMillis().toString(),
                            dateStr = targetDateStr,
                            time = targetTime,
                            title = "MPASI $selectedMealTime",
                            subtitle = selectedRecipe?.title ?: "",
                            recipeSteps = "Bahan: ${selectedRecipe?.ingredients?.joinToString(", ")}",
                            iconEmoji = "🥣",
                            iconBgColor = Color(0xFFFFE4E6),
                            isDone = false
                        )
                        babyViewModel.addSchedule(newItem)
                        showSaveDialog = false
                        onNavigate("schedule")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Simpan & Lihat Jadwal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Batal", color = SoftText)
                }
            }
        )
    }

    // --- DIALOG INFO CARA KERJA ---
    if (showInfoBottomSheet) {
        AlertDialog(
            onDismissRequest = { showInfoBottomSheet = false },
            confirmButton = {
                Button(
                    onClick = { showInfoBottomSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB7185)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mengerti", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "🎲 Cara Kerja Dadu MPASI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column {
                    Text(
                        text = "Dadu akan memilih menu MPASI secara acak berdasarkan:\n" +
                                "✅ Usia bayi\n" +
                                "✅ Pantangan/alergi yang dipilih\n" +
                                "✅ Variasi menu agar tidak berulang",
                        fontSize = 13.sp,
                        color = SoftText,
                        lineHeight = 20.sp
                    )
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// Helper Composable untuk Baris Tabel Anjuran MPASI
@Composable
fun MpasiguideRow(age: String, texture: String, freq: String, portion: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(age, modifier = Modifier.weight(1f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Text(texture, modifier = Modifier.weight(1.2f), fontSize = 9.sp, color = SoftText)
        Text(freq, modifier = Modifier.weight(1f), fontSize = 9.sp, color = SoftText)
        Text(portion, modifier = Modifier.weight(1.2f), fontSize = 9.sp, color = SoftText)
    }
}