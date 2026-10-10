package com.ecosnap.bharat.ui.diagnostic

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Verified
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.LanguageSelectionDialog
import com.ecosnap.bharat.ui.common.LanguageSelectorButton
import com.ecosnap.bharat.ui.common.WasteCategoryIcon

@Composable
fun DiagnosticScreen(
    itemId: String,
    repository: DataRepository,
    onBack: () -> Unit,
    onViewSolutions: (String) -> Unit
) {
    val currentLanguage by repository.appLanguage.collectAsState()
    val item = repository.getItem(itemId)
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { repository.setAppLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Selected answers map: questionId -> optionId
    val selectedAnswers = remember {
        mutableStateMapOf<String, String>().apply {
            item.diagnosticQuestions.forEach { q ->
                this[q.questionId] = q.options.first().id
            }
        }
    }

    var isSpeaking by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Navigation with Back & Language selector
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
            Text(
                text = if (currentLanguage == AppLanguage.ENGLISH) "AI Diagnosis" else "पहचान और स्थिति",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(8.dp))
            LanguageSelectorButton(
                currentLanguage = currentLanguage,
                onClick = { showLanguageDialog = true }
            )
        }

        // Identified Item Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = PaleMint)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WasteCategoryIcon(category = item.materialCategory, size = 52.dp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeemGreen
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) "AI Verified" else "AI द्वारा सत्यापित",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.getItemName(currentLanguage),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepCharcoal
                    )
                    Text(
                        text = "${item.getCategory(currentLanguage)} • ${item.getCondition(currentLanguage)}",
                        fontSize = 12.sp,
                        color = SlateGray
                    )
                }
            }
        }

        // Conversational AI Dialogue Box with Voice Button
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = NeemGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "AI Companion Advice:" else "AI साथी की सलाह:",
                            fontWeight = FontWeight.Bold,
                            color = NeemGreenDark,
                            fontSize = 14.sp
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSpeaking) SuryaAmber else PaleMint,
                        modifier = Modifier.clickable { isSpeaking = !isSpeaking }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isSpeaking) Color.White else NeemGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (currentLanguage == AppLanguage.ENGLISH) {
                                    if (isSpeaking) "Speaking..." else "Play Voice"
                                } else {
                                    if (isSpeaking) "बोल रहा है..." else "आवाज सुनें"
                                },
                                color = if (isSpeaking) Color.White else NeemGreenDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) {
                        "This is valuable material! Instead of discarding, you can upcycle or sell it. Answer these 2 quick questions to get customized solutions:"
                    } else {
                        "यह उपयोगी सामग्री है! इसे फेंकने के बजाय काम में ला सकते हैं। आपके लिए सही उपाय बताने के लिए बस इन 2 सवालों का जवाब दें:"
                    },
                    fontSize = 14.sp,
                    color = DeepCharcoal,
                    lineHeight = 20.sp
                )
            }
        }

        // Diagnostic Questions
        item.diagnosticQuestions.forEachIndexed { index, q ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) {
                            "Question ${index + 1}: ${q.getQuestion(currentLanguage)}"
                        } else {
                            "सवाल ${index + 1}: ${q.getQuestion(currentLanguage)}"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DeepCharcoal
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        q.options.forEach { opt ->
                            val isSelected = selectedAnswers[q.questionId] == opt.id
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) NeemGreen else PaleMint,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedAnswers[q.questionId] = opt.id }
                            ) {
                                Text(
                                    text = opt.getLabel(currentLanguage),
                                    color = if (isSelected) Color.White else DeepCharcoal,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CTA: View Solutions
        Button(
            onClick = { onViewSolutions(item.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeemGreen)
        ) {
            Text(
                text = if (currentLanguage == AppLanguage.ENGLISH) "View 3 Solutions (समाधान देखें)" else "3 समाधान देखें (View Solutions)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
