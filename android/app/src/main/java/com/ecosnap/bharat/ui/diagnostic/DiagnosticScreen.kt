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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.LanguageSelectionDialog
import com.ecosnap.bharat.ui.common.LanguageSelectorButton

@Composable
fun DiagnosticScreen(
    itemId: String,
    repository: DataRepository,
    onBack: () -> Unit,
    onViewSolutions: (String) -> Unit
) {
    val currentLanguage by repository.appLanguage.collectAsState()
    val item = repository.sampleItems.find { it.id == itemId } ?: repository.sampleItems.first()
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Text(text = "←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                }
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "AI Diagnosis" else "पहचान और स्थिति (AI Diagnosis)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepCharcoal
                )
            }
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
                Text(text = item.iconEmoji, fontSize = 48.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NeemGreen
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) "✓ AI Verified" else "✓ AI द्वारा सत्यापित",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
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
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "🤖 AI Companion Advice:" else "🤖 AI साथी की सलाह:",
                        fontWeight = FontWeight.Bold,
                        color = NeemGreenDark,
                        fontSize = 14.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSpeaking) SuryaAmber else PaleMint,
                        modifier = Modifier.clickable { isSpeaking = !isSpeaking }
                    ) {
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) {
                                if (isSpeaking) "🔊 Speaking..." else "▶️ Play Voice"
                            } else {
                                if (isSpeaking) "🔊 बोल रहा है..." else "▶️ आवाज सुनें"
                            },
                            color = if (isSpeaking) Color.White else NeemGreenDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
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
                text = if (currentLanguage == AppLanguage.ENGLISH) "✨ View 3 Solutions (समाधान देखें)" else "✨ 3 समाधान देखें (View Solutions)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
