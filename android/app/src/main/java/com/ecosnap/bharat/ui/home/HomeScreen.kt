package com.ecosnap.bharat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosnap.bharat.data.AppMode
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*

@Composable
fun HomeScreen(
    repository: DataRepository,
    onNavigateToScan: () -> Unit,
    onNavigateToScrapRates: () -> Unit,
    onNavigateToSociety: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onItemSelected: (String) -> Unit
) {
    val currentMode by repository.appMode.collectAsState()
    val userStats by repository.userStats.collectAsState()
    val societyStats by repository.societyStats.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    val currentLang by repository.currentLanguage.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Top App Header & Mode Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode Toggle Button
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (currentMode == AppMode.FAMILY) PaleMint else SuryaAmberLight,
                    modifier = Modifier.clickable { repository.toggleAppMode() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (currentMode == AppMode.FAMILY) "🏡 घर मोड (Family)" else "👥 समाज मोड (Society)",
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == AppMode.FAMILY) NeemGreenDark else DeepCharcoal,
                            fontSize = 14.sp
                        )
                        Text(text = "⇄ बदलें", fontSize = 12.sp, color = SlateGray)
                    }
                }

                // User Profile & Language Selector
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Language Switcher Pill
                    val langLabel = when (currentLang) {
                        "hinglish" -> "🌐 Hinglish"
                        "en" -> "🌐 English"
                        else -> "🌐 हिन्दी"
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFF1F3F4),
                        modifier = Modifier.clickable {
                            val nextLang = when (currentLang) {
                                "hi" -> "hinglish"
                                "hinglish" -> "en"
                                else -> "hi"
                            }
                            repository.setLanguage(nextLang)
                        }
                    ) {
                        Text(
                            text = langLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = DeepCharcoal
                        )
                    }

                    // Account / Profile Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (currentUser != null) PaleMint else Color(0xFFF1F3F4),
                        modifier = Modifier.clickable { onNavigateToAuth() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = if (currentUser != null) "👤 ${currentUser?.name}" else "👤 खाता",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser != null) NeemGreenDark else DeepCharcoal
                            )
                        }
                    }
                }
            }
        }

        // Greeting
        item {
            Column {
                Text(
                    text = if (currentUser != null) "नमस्ते, ${currentUser?.name}! 🙏" else "नमस्ते! 🙏",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                Text(
                    text = if (currentMode == AppMode.FAMILY)
                        "कचरे को फेंकें नहीं, काम में लाएं या बेचें।"
                    else
                        societyStats.name,
                    fontSize = 14.sp,
                    color = SlateGray
                )
            }
        }

        // Stats Card (Ghar vs Samaj Mode)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaleMint)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (currentMode == AppMode.FAMILY) "🌟 आपका पर्यावरण और बचत स्कोर" else "🏆 सोसाइटी कम्युनिटी रिकॉर्ड",
                        fontWeight = FontWeight.Bold,
                        color = NeemGreenDark,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentMode == AppMode.FAMILY) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(title = "बचाए गए पैसे", value = "₹${userStats.totalSavedInr}", highlightColor = RupeeGold)
                            StatBox(title = "डायवर्ट कचरा", value = "${userStats.totalWasteDivertedKg} kg", highlightColor = NeemGreen)
                            StatBox(title = "खाद / एंजाइम", value = "${userStats.totalCompostOrEnzymeLiters} L", highlightColor = SuryaAmber)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(title = "कुल खाद", value = "${societyStats.totalCompostKg} kg", highlightColor = NeemGreen)
                            StatBox(title = "डायवर्ट कचरा", value = "${societyStats.wasteDivertedPercentage}%", highlightColor = SuryaAmber)
                            StatBox(title = "कबाड़ आय", value = "₹${societyStats.communityScrapRevenueInr}", highlightColor = RupeeGold)
                        }
                    }
                }
            }
        }

        // HERO Action Button (Scan Waste)
        item {
            Button(
                onClick = onNavigateToScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeemGreen)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "📸", fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "कचरे का फोटो खींचें (Scan Waste)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "AI तुरंत बताएगा: जुगाड़, ऊर्जा या कबाड़ी भाव",
                            fontSize = 11.sp,
                            color = Color(0xFFD4EED8)
                        )
                    }
                }
            }
        }

        // AI Companion & History Quick Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // EcoSaathi AI Voice Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToChat() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SuryaAmberLight),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🤖", fontSize = 24.sp)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuryaAmber
                            ) {
                                Text(
                                    text = "आवाज 🎙️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "इको-साथी AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "बोलकर पूछें या चैट करें",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }

                // Scan & Chat History Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToHistory() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PaleMint),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📜", fontSize = 24.sp)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeemGreen
                            ) {
                                Text(
                                    text = "रिकॉर्ड",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "मेरा इतिहास",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "स्कैन व बातचीत सूची",
                            fontSize = 11.sp,
                            color = SlateGray
                        )
                    }
                }
            }
        }

        // Live Scrap Rates Ticker Preview
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToScrapRates() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RupeeGoldLight)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "💰 आज का कबाड़ भाव (Live Scrap Index)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "अखबार ₹15/kg • लोहा ₹30/kg • बोतल ₹12/kg",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                    Text(text = "सभी देखें ›", fontWeight = FontWeight.Bold, color = NeemGreen, fontSize = 13.sp)
                }
            }
        }

        // Sample Quick Pick Items
        item {
            Text(
                text = "⚡ सीधे जांचें (Quick Preset Demos):",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DeepCharcoal
            )
        }

        items(repository.sampleItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemSelected(item.id) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.iconEmoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.itemNameHindi, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DeepCharcoal)
                        Text(text = item.materialCategory, fontSize = 12.sp, color = SlateGray)
                    }
                    Text(text = "समाधान देखें ›", fontSize = 13.sp, color = NeemGreen, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Society Feature Entry
        if (currentMode == AppMode.SOCIETY) {
            item {
                Button(
                    onClick = onNavigateToSociety,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SuryaAmber)
                ) {
                    Text(text = "👥 सोसाइटी खाद पिट व पिकअप स्थिति देखें", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, highlightColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = highlightColor)
        Text(text = title, fontSize = 11.sp, color = SlateGray)
    }
}
