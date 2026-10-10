package com.ecosnap.bharat.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.AppMode
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.WasteCategoryIcon

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
    val appLanguage by repository.appLanguage.collectAsState()
    val isEn = appLanguage == AppLanguage.ENGLISH

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // 1. Top Navigation Bar (Brand + Language Toggle + Account)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Logo & Brand Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PaleMint,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Eco,
                                contentDescription = "EcoSnap",
                                tint = NeemGreenDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "EcoSnap Bharat",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = NeemGreenDark,
                            maxLines = 1
                        )
                        Text(
                            text = if (isEn) "Waste to Wealth & Jugaad" else "स्वच्छ भारत • कबाड़ से जुगाड़",
                            fontSize = 10.sp,
                            color = SlateGray,
                            maxLines = 1
                        )
                    }
                }

                // Top Action Pills (Compact, Guaranteed no overflow)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1-Tap Language Toggle Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isEn) NeemGreen else Color(0xFFE8F5E9),
                        modifier = Modifier.clickable { repository.toggleLanguage() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "Language",
                                tint = if (isEn) Color.White else NeemGreenDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (isEn) "EN" else "हि",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isEn) Color.White else NeemGreenDark
                            )
                        }
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
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Account",
                                tint = if (currentUser != null) NeemGreenDark else DeepCharcoal,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = if (currentUser != null) currentUser?.name?.take(6) ?: "User" else (if (isEn) "Account" else "खाता"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser != null) NeemGreenDark else DeepCharcoal,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 2. Full-Width Segmented Mode Bar (Family vs Society)
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFEAEFEB),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Family Mode Tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentMode == AppMode.FAMILY) NeemGreen else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (currentMode != AppMode.FAMILY) repository.toggleAppMode() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = if (currentMode == AppMode.FAMILY) Color.White else SlateGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEn) "Family Mode" else "घर मोड",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMode == AppMode.FAMILY) Color.White else SlateGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Society Hub Tab
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (currentMode == AppMode.SOCIETY) SuryaAmber else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { if (currentMode != AppMode.SOCIETY) repository.toggleAppMode() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = if (currentMode == AppMode.SOCIETY) Color.White else SlateGray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEn) "Society Hub" else "समाज मोड",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMode == AppMode.SOCIETY) Color.White else SlateGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 3. User Greeting
        item {
            Column {
                Text(
                    text = if (currentUser != null)
                        (if (isEn) "Welcome back, ${currentUser?.name}" else "नमस्ते, ${currentUser?.name}")
                    else
                        (if (isEn) "Welcome to EcoSnap" else "इको-स्नैप में आपका स्वागत है"),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
                Text(
                    text = if (currentMode == AppMode.FAMILY)
                        (if (isEn) "Don't throw waste—upcycle into DIY Jugaad or sell at fair rates." else "कचरे को फेंकें नहीं, काम में लाएं या बेचें।")
                    else
                        societyStats.name,
                    fontSize = 13.sp,
                    color = SlateGray,
                    lineHeight = 18.sp
                )
            }
        }

        // 4. Stats Card (Ghar vs Samaj Mode) - 3 Equal Balanced Columns
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaleMint)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (currentMode == AppMode.FAMILY) Icons.Default.TrendingUp else Icons.Default.Leaderboard,
                            contentDescription = null,
                            tint = NeemGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (currentMode == AppMode.FAMILY)
                                (if (isEn) "Savings & Impact Score" else "पर्यावरण और बचत स्कोर")
                            else
                                (if (isEn) "Community Impact Score" else "सोसाइटी कम्युनिटी रिकॉर्ड"),
                            fontWeight = FontWeight.Bold,
                            color = NeemGreenDark,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    if (currentMode == AppMode.FAMILY) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                StatBox(
                                    title = if (isEn) "Money Saved" else "बचाए गए पैसे",
                                    value = "₹${userStats.totalSavedInr}",
                                    highlightColor = RupeeGold
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                StatBox(
                                    title = if (isEn) "Waste Diverted" else "डायवर्ट कचरा",
                                    value = "${userStats.totalWasteDivertedKg} kg",
                                    highlightColor = NeemGreen
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                StatBox(
                                    title = if (isEn) "Bio-Enzyme" else "खाद / एंजाइम",
                                    value = "${userStats.totalCompostOrEnzymeLiters} L",
                                    highlightColor = SuryaAmber
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                StatBox(
                                    title = if (isEn) "Total Compost" else "कुल खाद",
                                    value = "${societyStats.totalCompostKg} kg",
                                    highlightColor = NeemGreen
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                StatBox(
                                    title = if (isEn) "Diverted" else "डायवर्ट कचरा",
                                    value = "${societyStats.wasteDivertedPercentage}%",
                                    highlightColor = SuryaAmber
                                )
                            }
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                StatBox(
                                    title = if (isEn) "Scrap Income" else "कबाड़ आय",
                                    value = "₹${societyStats.communityScrapRevenueInr}",
                                    highlightColor = RupeeGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. HERO Action Button (Scan Waste with Real Camera) - Fully Responsive
        item {
            Button(
                onClick = onNavigateToScan,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 72.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeemGreen),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEn) "Scan Waste with Camera" else "कचरे का फोटो खींचें (Scan Waste)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEn) "AI detects: DIY Jugaad, Clean Energy & Scrap Value" else "AI तुरंत बताएगा: जुगाड़, ऊर्जा या कबाड़ी भाव",
                            fontSize = 11.sp,
                            color = Color(0xFFD4EED8),
                            lineHeight = 15.sp,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // 6. AI Companion & History Quick Cards (50/50 Balanced Flex)
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
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "EcoSaathi AI",
                                tint = SuryaAmber,
                                modifier = Modifier.size(26.dp)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SuryaAmber
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = if (isEn) "Voice" else "आवाज",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isEn) "EcoSaathi AI" else "इको-साथी AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepCharcoal,
                            maxLines = 1
                        )
                        Text(
                            text = if (isEn) "Voice & chat companion" else "बोलकर पूछें या चैट करें",
                            fontSize = 11.sp,
                            color = SlateGray,
                            maxLines = 2,
                            lineHeight = 15.sp
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
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = NeemGreen,
                                modifier = Modifier.size(26.dp)
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NeemGreen
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = if (isEn) "Synced" else "रिकॉर्ड",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isEn) "My History" else "मेरा इतिहास",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = DeepCharcoal,
                            maxLines = 1
                        )
                        Text(
                            text = if (isEn) "Scans & chat logs" else "स्कैन व बातचीत सूची",
                            fontSize = 11.sp,
                            color = SlateGray,
                            maxLines = 2,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // 7. Live Scrap Rates Ticker (Fixed: Column weight(1f) prevents View All vertical wrapping!)
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                tint = RupeeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isEn) "Today's Scrap Market Rates" else "आज का कबाड़ मंडी भाव",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = DeepCharcoal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEn) "Newspaper ₹15/kg • Iron ₹30/kg • PET Bottle ₹12/kg" else "अखबार ₹15/kg • लोहा ₹30/kg • बोतल ₹12/kg",
                            fontSize = 11.sp,
                            color = SlateGray,
                            maxLines = 2,
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeemGreen.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (isEn) "View all ›" else "सभी देखें ›",
                            fontWeight = FontWeight.Bold,
                            color = NeemGreenDark,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // 8. Sample Quick Pick Items
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = DeepCharcoal,
                    modifier = Modifier.size(17.dp)
                )
                Text(
                    text = if (isEn) "Quick Waste Samples:" else "सीधे जांचें (Waste Samples):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepCharcoal
                )
            }
        }

        items(repository.sampleItems) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemSelected(item.id) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WasteCategoryIcon(category = item.materialCategory, containerSize = 42.dp, iconSize = 22.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEn) item.itemNameEnglish else item.itemNameHindi,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = DeepCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.materialCategory,
                            fontSize = 12.sp,
                            color = SlateGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEn) "View solutions ›" else "समाधान देखें ›",
                        fontSize = 12.sp,
                        color = NeemGreen,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        softWrap = false
                    )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) "View Society Compost Pits & Bulk Pickups" else "सोसाइटी खाद पिट व पिकअप स्थिति देखें",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(title: String, value: String, highlightColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = highlightColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            color = SlateGray,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}
