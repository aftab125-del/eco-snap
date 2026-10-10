package com.ecosnap.bharat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.ecosnap.bharat.data.ChatMessage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import kotlinx.coroutines.launch

@Composable
fun AIChatScreen(
    repository: DataRepository,
    onBack: () -> Unit
) {
    val messages by repository.chatMessages.collectAsState()
    val currentLang by repository.currentLanguage.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var activeSpeakingId by remember { mutableStateOf<String?>(null) }
    var isListeningVoice by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickChips = when (currentLang) {
        "hinglish" -> listOf(
            "Plastic bottle ka jugaad?",
            "Lemon peel bio-enzyme recipe?",
            "Aaj ka kabadi scrap rate?",
            "Mango seed compost mein daal sakte hain?"
        )
        "en" -> listOf(
            "Plastic bottle DIY upcycling?",
            "How to make bio-enzyme?",
            "Today's scrap market rates?",
            "Can I compost fruit seeds?"
        )
        else -> listOf(
            "प्लास्टिक बोतल का क्या जुगाड़ बनाएं?",
            "नींबू के छिलके से बायो-एंजाइम कैसे बनाएं?",
            "आज का कबाड़ी मंडी भाव क्या है?",
            "क्या आम की गुठली से खाद बनती है?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar with Language Switcher
        Surface(
            color = SurfaceCard,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Text(text = "←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                        }
                        Column {
                            Text(
                                text = "🤖 इको-साथी AI (EcoSaathi)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DeepCharcoal
                            )
                            Text(
                                text = "आवाज और स्मार्ट चैट साथी (AI Companion)",
                                fontSize = 11.sp,
                                color = NeemGreen
                            )
                        }
                    }

                    // Language Chips in Header
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("hi" to "हिन्दी", "hinglish" to "Hinglish", "en" to "EN").forEach { (code, label) ->
                            val isSelected = currentLang == code
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) NeemGreen else Color(0xFFF1F3F4),
                                modifier = Modifier.clickable { repository.setLanguage(code) }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else DeepCharcoal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(
                    message = msg,
                    isSpeaking = activeSpeakingId == msg.id,
                    onToggleVoice = {
                        activeSpeakingId = if (activeSpeakingId == msg.id) null else msg.id
                    }
                )
            }
        }

        // Voice Listening Indicator Banner
        if (isListeningVoice) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SuryaAmberLight),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎙️", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "सुन रहे हैं... बोलिए (Listening in $currentLang)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SuryaAmber
                        )
                    }
                    TextButton(onClick = { isListeningVoice = false }) {
                        Text(text = "रोकें", color = TerracottaRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickChips) { chipText ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PaleMint,
                    modifier = Modifier.clickable {
                        repository.sendMessageToAI(chipText)
                    }
                ) {
                    Text(
                        text = chipText,
                        fontSize = 12.sp,
                        color = NeemGreenDark,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input Bar
        Surface(
            color = SurfaceCard,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Microphone Voice Input Button
                IconButton(
                    onClick = {
                        isListeningVoice = !isListeningVoice
                        if (isListeningVoice) {
                            // Simulate quick voice query after 1.5 seconds if speech not hardware-linked
                            scope.launch {
                                kotlinx.coroutines.delay(1500)
                                isListeningVoice = false
                                val sampleVoiceQuery = if (currentLang == "hi")
                                    "क्या संतरे के छिलकों से फर्श क्लीनर बनता है?"
                                else
                                    "Can I make cleaner from orange peels?"
                                repository.sendMessageToAI(sampleVoiceQuery, isVoice = true)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (isListeningVoice) SuryaAmber else PaleMint, CircleShape)
                ) {
                    Text(text = "🎙️", fontSize = 20.sp)
                }

                // Text Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("कचरे या जुगाड़ के बारे में पूछें...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3
                )

                // Send Button
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            repository.sendMessageToAI(inputText.trim())
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank(),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = NeemGreen),
                    modifier = Modifier.size(44.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "➤", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isSpeaking: Boolean,
    onToggleVoice: () -> Unit
) {
    val isUser = message.sender == "USER"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                shape = CircleShape,
                color = PaleMint,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🤖", fontSize = 16.sp)
                }
            }
        }

        Card(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) NeemGreen else SurfaceCard
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    color = if (isUser) Color.White else DeepCharcoal,
                    lineHeight = 19.sp
                )

                // Voice playback button for AI responses
                if (!isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSpeaking) SuryaAmberLight else PaleMint,
                        modifier = Modifier.clickable { onToggleVoice() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = if (isSpeaking) "⏸️ रोकें" else "🔊 आवाज सुनें", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeemGreenDark)
                        }
                    }
                }
            }
        }
    }
}
