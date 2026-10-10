package com.ecosnap.bharat.ui.society

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalShipping
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.LanguageSelectionDialog
import com.ecosnap.bharat.ui.common.LanguageSelectorButton

@Composable
fun SocietyScreen(
    repository: DataRepository,
    onBack: () -> Unit
) {
    val currentLanguage by repository.appLanguage.collectAsState()
    val societyStats by repository.societyStats.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { repository.setAppLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = DeepCharcoal
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = null,
                        tint = DeepCharcoal,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Society Hub" else "समाज व पंचायत हब",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = societyStats.getName(currentLanguage),
                    fontSize = 11.sp,
                    color = SlateGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            LanguageSelectorButton(
                currentLanguage = currentLanguage,
                onClick = { showLanguageDialog = true }
            )
        }

        // Community Landfill Diversion Scoreboard
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
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = null,
                        tint = NeemGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Monthly Community Record" else "इस महीने का सामूहिक रिकॉर्ड",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NeemGreenDark
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${societyStats.totalCompostKg} kg", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeemGreen)
                        Text(text = if (currentLanguage == AppLanguage.ENGLISH) "Society Compost" else "सोसाइटी खाद", fontSize = 11.sp, color = SlateGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${societyStats.wasteDivertedPercentage}%", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SuryaAmber)
                        Text(text = if (currentLanguage == AppLanguage.ENGLISH) "Landfill Diversion" else "लैंडफिल डायवर्जन", fontSize = 11.sp, color = SlateGray)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "₹${societyStats.communityScrapRevenueInr}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RupeeGold)
                        Text(text = if (currentLanguage == AppLanguage.ENGLISH) "Scrap Income" else "कबाड़ बिक्री आय", fontSize = 11.sp, color = SlateGray)
                    }
                }
            }
        }

        // Community Compost Pits Tracker
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = NeemGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Community Compost Pit Status:" else "कम्युनिटी कम्पोस्ट गड्ढा स्थिति:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepCharcoal
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PaleMint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Pit #1: Park Compost Pit" else "गड्ढा #1: पार्क खाद पिट",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(text = societyStats.getPit1Status(currentLanguage), color = NeemGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuryaAmberLight,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Pit #2: Block-B Kitchen Pit" else "गड्ढा #2: ब्लॉक-B किचन पिट",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(text = societyStats.getPit2Status(currentLanguage), color = SuryaAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Bulk Recyclable Pickup
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = NeemGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Community Bulk Scrap Pickup:" else "सामूहिक कबाड़ पिकअप (Bulk Pickup):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepCharcoal
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) {
                        "Next Pickup: ${societyStats.getNextBulkPickupDate(currentLanguage)}"
                    } else {
                        "अगला पिकअप: ${societyStats.getNextBulkPickupDate(currentLanguage)}"
                    },
                    fontSize = 13.sp,
                    color = NeemGreenDark,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "Dealers offer 10% higher rates for collective bulk pickups." else "सामूहिक रूप से बेचने पर कबाड़ी 10% ज्यादा भाव देता है।",
                    fontSize = 12.sp,
                    color = SlateGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* Add to bulk queue */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeemGreen)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Add my dry scrap (8 kg) to pickup" else "मेरा सूखा कबाड़ (8 kg) पिकअप में जोड़ें",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
