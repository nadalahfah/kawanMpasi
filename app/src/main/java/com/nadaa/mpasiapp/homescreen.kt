package com.nadaa.mpasiapp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- PALET WARNA UTAMA ---
val BackgroundPink = Color(0xFFFFF0F0)
val YellowCardBg = Color(0xFFFFF5DC)
val WhiteCardBg = Color(0xFFFFFFFF)
val PurpleCardBg = Color(0xFFFAF5FF)
val PurpleAccent = Color(0xFF8B5CF6)
val BlueAccent = Color(0xFF60A5FA)
val BottomNavBgColor = Color(0xFFA6E39D)
val DarkText = Color(0xFF331811)
val SoftText = Color(0xFF7A6863)

val BackgroundColor = BackgroundPink
val YellowCardColor = YellowCardBg
val PurpleCardColor = PurpleCardBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    babyViewModel: BabyViewModel = BabyViewModel(),
    currentRoute: String = "home",
    onNavigate: (String) -> Unit = {}
) {
    val babyData by babyViewModel.babyState.collectAsState()
    var showNotificationDialog by remember { mutableStateOf(false) }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = WhiteCardBg,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏰ ", fontSize = 22.sp)
                    Text(
                        text = "Pengingat Masak MPASI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Halo Bunda! 👋",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Jadwal MPASI ${babyData.name} selanjutnya akan dimulai 1 jam lagi (08.00 WIB).\n\nYuk persiapkan bahan dan mulai memasak 'Bubur Ayam Wortel' sekarang biar si kecil siap makan tepat waktu! 🥣✨",
                        fontSize = 13.sp,
                        color = SoftText
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationDialog = false
                        babyViewModel.clearNotifications()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BottomNavBgColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Siap, Mulai Masak! 👩‍🍳", color = DarkText, fontWeight = FontWeight.Bold)
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
                .background(BackgroundPink)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // --- 1. HEADER (Sapaan & Lonceng Clickable + BADGE ANGKA) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halo, Ayah/Bunda! 👋",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    Text(
                        text = "Semangat hari ini untuk ${babyData.name} 💖",
                        fontSize = 13.sp,
                        color = SoftText
                    )
                }

                // Lonceng Notifikasi dengan Badge Angka
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { showNotificationDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(WhiteCardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🔔", fontSize = 20.sp)
                    }

                    // BADGE BADGE COUNTER NOTIFIKASI
                    if (babyData.unreadNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF3B30))
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${babyData.unreadNotificationCount}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 2. KARTU PROFIL BAYI ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = YellowCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Image(
                                painter = painterResource(id = R.drawable.baby),
                                contentDescription = "Foto Bayi",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF8A8A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📷", fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = babyData.name,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText
                                )
                                Text(" 🌸", fontSize = 15.sp)
                            }
                            Text(
                                text = babyData.ageDetailString,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = DarkText
                            )
                            Text(
                                text = "Tgl Lahir: ${babyData.birthDate}",
                                fontSize = 12.sp,
                                color = SoftText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                color = Color(0xFFDCFCE7),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Gizi Normal ",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                    Text("✔", fontSize = 10.sp, color = Color(0xFF15803D))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricSmallCard(modifier = Modifier.weight(1f), icon = "⚖️", label = "Berat", value = "${babyData.weightKg}", unit = "kg")
                        MetricSmallCard(modifier = Modifier.weight(1f), icon = "📏", label = "Tinggi", value = "${babyData.heightCm}", unit = "cm")
                        MetricSmallCard(modifier = Modifier.weight(1f), icon = "🗣️", label = "L. Kepala", value = "${babyData.headCircumferenceCm}", unit = "cm")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. GRAFIK BERAT BADAN ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📈 ", fontSize = 16.sp)
                            Text("Grafik Berat Badan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        }
                        Text("Riwayat Terakhir ▾", fontSize = 11.sp, color = SoftText)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LineChartWithAxis(data = if (babyData.weightHistory.isEmpty()) listOf(0f) else babyData.weightHistory)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 4. GRAFIK TINGGI BADAN ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PurpleCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📏 ", fontSize = 16.sp)
                            Text("Grafik Tinggi Badan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        }
                        Text("Riwayat Terakhir ▾", fontSize = 11.sp, color = SoftText)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BarChartWithAxis(data = if (babyData.heightHistory.isEmpty()) listOf(0f) else babyData.heightHistory)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- 5. GRID MPASI ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = YellowCardBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🥣 ", fontSize = 13.sp)
                            Text("MPASI Hari Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.baby),
                                contentDescription = "Foto MPASI",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text("Bubur Ayam Wortel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
                                Text("08.00 WIB", fontSize = 10.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(0.9f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = YellowCardBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔔 ", fontSize = 13.sp)
                            Text("Pengingat", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Vitamin D", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text("10.00 WIB", fontSize = 10.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun LineChartWithAxis(data: List<Float>) {
    val months = listOf("Feb", "Mar", "Apr", "Mei", "Jun", "Jul")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WhiteCardBg)
            .padding(top = 18.dp, bottom = 8.dp, start = 28.dp, end = 16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height - 24f
            val maxData = (data.maxOrNull() ?: 10f).coerceAtLeast(10f)
            val minData = (data.minOrNull() ?: 5f).coerceAtMost(5f)

            val spacing = if (data.size > 1) w / (data.size - 1) else w
            val path = Path()

            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#7A6863")
                textSize = 24f
                isAntiAlias = true
            }
            val valuePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#331811")
                textSize = 25f
                isFakeBoldText = true
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText("kg", -24f, 10f, textPaint)

            data.forEachIndexed { index, value ->
                val x = index * spacing
                val y = h - ((value - minData) / ((maxData - minData).let { if (it == 0f) 1f else it }) * (h - 20f))

                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

                drawCircle(color = PurpleAccent, radius = 6f, center = Offset(x, y))
                drawCircle(color = Color.White, radius = 3f, center = Offset(x, y))

                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(value),
                    x - 18f,
                    y - 12f,
                    valuePaint
                )

                if (index < months.size) {
                    drawContext.canvas.nativeCanvas.drawText(
                        months[index],
                        x - 18f,
                        size.height,
                        textPaint
                    )
                }
            }

            drawPath(path = path, color = PurpleAccent, style = Stroke(width = 4f))
        }
    }
}

@Composable
fun BarChartWithAxis(data: List<Float>) {
    val months = listOf("Feb", "Mar", "Apr", "Mei", "Jun", "Jul")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WhiteCardBg)
            .padding(top = 18.dp, bottom = 8.dp, start = 28.dp, end = 16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height - 24f
            val maxData = (data.maxOrNull() ?: 85f).coerceAtLeast(85f)
            val minData = (data.minOrNull() ?: 50f).coerceAtMost(50f)

            val barWidth = (w / data.size) * 0.4f
            val spacing = w / data.size

            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#7A6863")
                textSize = 24f
                isAntiAlias = true
            }
            val valuePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#331811")
                textSize = 25f
                isFakeBoldText = true
                isAntiAlias = true
            }

            drawContext.canvas.nativeCanvas.drawText("cm", -24f, 10f, textPaint)

            data.forEachIndexed { index, value ->
                val barHeight = ((value - minData) / ((maxData - minData).let { if (it == 0f) 1f else it })) * (h - 20f)
                val x = index * spacing + (spacing - barWidth) / 2
                val y = h - barHeight

                drawRoundRect(
                    color = BlueAccent,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    "%.1f".format(value),
                    x - 8f,
                    y - 10f,
                    valuePaint
                )

                if (index < months.size) {
                    drawContext.canvas.nativeCanvas.drawText(
                        months[index],
                        x - 2f,
                        size.height,
                        textPaint
                    )
                }
            }
        }
    }
}

@Composable
fun MetricSmallCard(
    modifier: Modifier = Modifier,
    icon: String,
    label: String,
    value: String,
    unit: String
) {
    Surface(
        modifier = modifier,
        color = WhiteCardBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(label, fontSize = 9.sp, color = SoftText)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text(" $unit", fontSize = 9.sp, color = DarkText)
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    color = BottomNavBgColor,
                    shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Top
        ) {
            NavItemFigma(
                iconResId = R.drawable.home,
                iconSize = 38.dp,
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") }
            )
            NavItemFigma(
                iconResId = R.drawable.bayi,
                iconSize = 40.dp,
                isSelected = currentRoute == "profile",
                onClick = { onNavigate("profile") }
            )
            NavItemFigma(
                iconResId = R.drawable.diceroller,
                iconSize = 40.dp,
                isSelected = currentRoute == "dice",
                onClick = { onNavigate("dice") }
            )
            NavItemFigma(
                iconResId = R.drawable.jadwal,
                iconSize = 48.dp,
                isSelected = currentRoute == "schedule",
                onClick = { onNavigate("schedule") }
            )
        }
    }
}

@Composable
fun NavItemFigma(
    iconResId: Int,
    iconSize: Dp = 36.dp,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedOffset by animateDpAsState(
        targetValue = if (isSelected) (-20).dp else -10.dp,
        animationSpec = tween(durationMillis = 300),
        label = "floatingAnimation"
    )

    val animatedSize by animateDpAsState(
        targetValue = if (isSelected) 75.dp else 50.dp,
        animationSpec = tween(durationMillis = 300),
        label = "sizeAnimation"
    )

    Box(
        modifier = Modifier
            .offset(y = animatedOffset)
            .size(animatedSize)
            .shadow(
                elevation = if (isSelected) 8.dp else 0.dp,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(if (isSelected) BottomNavBgColor else Color.Transparent)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(iconSize)
        )
    }
}

