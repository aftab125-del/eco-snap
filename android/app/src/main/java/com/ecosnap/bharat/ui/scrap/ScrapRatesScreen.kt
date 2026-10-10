package com.ecosnap.bharat.ui.scrap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Lightbulb
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.LanguageSelectionDialog
import com.ecosnap.bharat.ui.common.LanguageSelectorButton
import com.ecosnap.bharat.ui.common.WasteCategoryIcon

@Composable
fun ScrapRatesScreen(
    repository: DataRepository,
    onBack: () -> Unit
) {
    val currentLanguage by repository.appLanguage.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { repository.setAppLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                            imageVector = Icons.Default.CurrencyRupee,
                            contentDescription = null,
                            tint = RupeeGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Today's Scrap Rates" else "कबाड़ मंडी भाव",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = DeepCharcoal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Delhi-NCR Avg (₹/kg)" else "राष्ट्रीय औसत दरें (₹/kg)",
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
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RupeeGoldLight)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = DeepCharcoal,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) {
                            "Tip: Separate iron and copper before weighing. Clean and dry scrap commands higher market rates."
                        } else {
                            "सलाह: लोहा और तांबा कबाड़ी को तौलने से पहले हमेशा अलग करें। सूखा और साफ कबाड़ ज्यादा भाव दिलाता है।"
                        },
                        fontSize = 13.sp,
                        color = DeepCharcoal,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        items(repository.scrapRates) { rate ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            WasteCategoryIcon(category = rate.category, size = 36.dp)
                            Column {
                                Text(
                                    text = rate.getName(currentLanguage),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DeepCharcoal
                                )
                                Text(
                                    text = "${rate.nameEnglish} • ${rate.category}",
                                    fontSize = 11.sp,
                                    color = SlateGray
                                )
                            }
                        }
                        Text(
                            text = "₹${rate.minPriceInr} - ₹${rate.maxPriceInr}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = RupeeGold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• ${rate.getTip(currentLanguage)}",
                        fontSize = 12.sp,
                        color = SlateGray,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
