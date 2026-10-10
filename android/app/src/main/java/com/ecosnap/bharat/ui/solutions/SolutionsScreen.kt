package com.ecosnap.bharat.ui.solutions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.VolumeUp
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.LanguageSelectionDialog
import com.ecosnap.bharat.ui.common.LanguageSelectorButton

@Composable
fun SolutionsScreen(
    itemId: String,
    repository: DataRepository,
    onBack: () -> Unit,
    onImpactLogged: () -> Unit
) {
    val currentLanguage by repository.appLanguage.collectAsState()
    val solutions = remember { repository.getSolutionsForItem(itemId) }
    var isAudioPlaying by remember { mutableStateOf(false) }
    var hasLogged by remember { mutableStateOf(false) }
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
        // Top Navigation, Audio Player & Language Selector
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isAudioPlaying) SuryaAmber else NeemGreen,
                    modifier = Modifier.clickable { isAudioPlaying = !isAudioPlaying }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) {
                                if (isAudioPlaying) "Pause" else "Listen"
                            } else {
                                if (isAudioPlaying) "रोकें" else "सुनें"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                LanguageSelectorButton(
                    currentLanguage = currentLanguage,
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        // Audio playing script banner
        if (isAudioPlaying) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SuryaAmberLight)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = SuryaAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "AI Voice Guidance:" else "AI वॉइस मार्गदर्शन:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SuryaAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = solutions.getSummarySpeech(currentLanguage),
                        fontSize = 13.sp,
                        color = DeepCharcoal,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Method 1: Ghar Ka Jugaad
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(6.dp), color = PaleMint) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = NeemGreenDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) "Method 1: DIY Home Upcycle" else "उपाय 1: घर का जुगाड़ (DIY Upcycle)",
                                color = NeemGreenDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Time: ${solutions.jugaad.timeMinutes} mins • Cost: ₹${solutions.jugaad.costInr}" else "समय: ${solutions.jugaad.timeMinutes} मिनट • खर्च: ₹${solutions.jugaad.costInr}",
                        fontSize = 11.sp,
                        color = SlateGray
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = solutions.jugaad.getTitle(currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepCharcoal
                )
                Spacer(modifier = Modifier.height(10.dp))
                for ((idx, step) in solutions.jugaad.getSteps(currentLanguage).withIndex()) {
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(text = "${idx + 1}. ", fontWeight = FontWeight.Bold, color = NeemGreen, fontSize = 14.sp)
                        Text(text = step, fontSize = 14.sp, color = DeepCharcoal, lineHeight = 20.sp)
                    }
                }
            }
        }

        // Method 2: Urja Aur Khad (Home Energy & Resource)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(shape = RoundedCornerShape(6.dp), color = SuryaAmberLight) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = SuryaAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Method 2: Energy & Resource" else "उपाय 2: ऊर्जा और संसाधन (Energy & Resource)",
                            color = SuryaAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = solutions.energyResource.getTitle(currentLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DeepCharcoal
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "Type: ${solutions.energyResource.getType(currentLanguage)}" else "प्रकार: ${solutions.energyResource.getType(currentLanguage)}",
                    fontSize = 11.sp,
                    color = SlateGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = solutions.energyResource.getMethod(currentLanguage),
                    fontSize = 14.sp,
                    color = DeepCharcoal,
                    lineHeight = 20.sp
                )
            }
        }

        // Method 3: Kabaad Se Kamai (Scrap Value & Disposal)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Surface(shape = RoundedCornerShape(6.dp), color = RupeeGoldLight) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Recycling,
                            contentDescription = null,
                            tint = DeepCharcoal,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "Method 3: Scrap Value & Disposal" else "उपाय 3: कबाड़ से कमाई (Scrap Value)",
                            color = DeepCharcoal,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Scrap Price: ${solutions.scrapDisposal.getRate(currentLanguage)}" else "कबाड़ी भाव: ${solutions.scrapDisposal.getRate(currentLanguage)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RupeeGold
                    )
                    Text(
                        text = solutions.scrapDisposal.getBinType(currentLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeemGreen
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "Prep Tip: ${solutions.scrapDisposal.getTip(currentLanguage)}" else "तैयारी सलाह: ${solutions.scrapDisposal.getTip(currentLanguage)}",
                    fontSize = 13.sp,
                    color = SlateGray
                )
            }
        }

        // Environmental Impact Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PaleMint)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        text = if (currentLanguage == AppLanguage.ENGLISH) "CO2 Prevented: ${solutions.impact.co2PreventedKg} kg" else "रोकी गई CO2: ${solutions.impact.co2PreventedKg} kg",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeemGreenDark
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Scale,
                        contentDescription = null,
                        tint = NeemGreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Weight: ${solutions.impact.weightGrams}g" else "वजन: ${solutions.impact.weightGrams}g",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = NeemGreenDark
                    )
                }
            }
        }

        // Log Impact CTA Button
        Button(
            onClick = {
                if (!hasLogged) {
                    repository.logSolutionAdopted(solutions.impact, scrapValueInr = 12)
                    hasLogged = true
                    onImpactLogged()
                }
            },
            enabled = !hasLogged,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (hasLogged) SlateGray else NeemGreen)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (hasLogged) Icons.Default.CheckCircle else Icons.Default.Done,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) {
                        if (hasLogged) "Impact Logged!" else "Adopted Solution (Log Impact)"
                    } else {
                        if (hasLogged) "प्रभाव दर्ज किया गया" else "मैंने यह उपाय अपना लिया (Log Impact)"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
