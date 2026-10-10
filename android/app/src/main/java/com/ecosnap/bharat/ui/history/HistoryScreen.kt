package com.ecosnap.bharat.ui.history

import androidx.compose.foundation.background
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
import com.ecosnap.bharat.data.ChatMessage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.data.ScanHistoryItem
import com.ecosnap.bharat.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val scanHistory by repository.scanHistory.collectAsState()
    val chatMessages by repository.chatMessages.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0 = Scans, 1 = Chats

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Surface(
            color = SurfaceCard,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Text(text = "←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepCharcoal)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📜 इतिहास व रिकॉर्ड (History)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DeepCharcoal
                        )
                        Text(
                            text = if (currentUser != null) "${currentUser?.name} • क्लाउड सिंक रिकॉर्ड" else "अतिथि मोड • स्थानीय रिकॉर्ड",
                            fontSize = 12.sp,
                            color = SlateGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tabs: Scans vs Chats
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PaleMint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 0) NeemGreen else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 }
                        ) {
                            Text(
                                text = "📸 स्कैन इतिहास (${scanHistory.size})",
                                color = if (selectedTab == 0) Color.White else DeepCharcoal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selectedTab == 1) NeemGreen else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 }
                        ) {
                            Text(
                                text = "💬 चैट इतिहास (${chatMessages.size})",
                                color = if (selectedTab == 1) Color.White else DeepCharcoal,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Tab Content
        if (selectedTab == 0) {
            // Scans List
            if (scanHistory.isEmpty()) {
                EmptyStateView(
                    icon = "📸",
                    title = "कोई स्कैन इतिहास नहीं मिला",
                    description = "आपने अभी तक किसी कचरे का स्कैन नहीं किया है। कैमरे से फोटो लें और तुरंत समाधान पाएं।",
                    buttonText = "📸 कचरा स्कैन करें",
                    onAction = onNavigateToScan
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                ) {
                    items(scanHistory) { item ->
                        ScanHistoryCard(item = item)
                    }
                }
            }
        } else {
            // Chat Messages List
            if (chatMessages.isEmpty()) {
                EmptyStateView(
                    icon = "🤖",
                    title = "कोई चैट इतिहास नहीं मिला",
                    description = "इको-साथी AI से कोई भी सवाल पूछें - कचरा रीसाइक्लिंग, घर का जुगाड़ या आज के कबाड़ी भाव।",
                    buttonText = "💬 इको-साथी से बात करें",
                    onAction = onNavigateToChat
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatHistoryCard(message = message, onNavigateToChat = onNavigateToChat)
                    }
                }
            }
        }
    }
}

@Composable
fun ScanHistoryCard(item: ScanHistoryItem) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(item.timestamp) {
        if (item.timestamp > 0) dateFormat.format(Date(item.timestamp)) else "हाल ही में"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.itemNameHindi,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DeepCharcoal
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PaleMint
                ) {
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeemGreenDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (item.itemNameEnglish.isNotBlank() && item.itemNameEnglish != item.itemNameHindi) {
                Text(
                    text = item.itemNameEnglish,
                    fontSize = 12.sp,
                    color = SlateGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Adopted Solution & Scrap Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💡", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.jugaadTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = DeepCharcoal
                    )
                }

                if (item.scrapRate.isNotBlank()) {
                    Text(
                        text = item.scrapRate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RupeeGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F3F4))
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "🕒 $formattedDate",
                fontSize = 11.sp,
                color = SlateGray
            )
        }
    }
}

@Composable
fun ChatHistoryCard(message: ChatMessage, onNavigateToChat: () -> Unit) {
    val isUser = message.sender == "USER"
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val formattedDate = remember(message.timestamp) { dateFormat.format(Date(message.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToChat() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUser) PaleMint else SurfaceCard
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = if (isUser) NeemGreen else SuryaAmberLight,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isUser) "👤" else "🤖",
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUser) "आपका सवाल" else "इको-साथी AI",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) NeemGreenDark else SuryaAmber
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 10.sp,
                        color = SlateGray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = DeepCharcoal,
                    maxLines = 3
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: String,
    title: String,
    description: String,
    buttonText: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = icon, fontSize = 54.sp)
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = SlateGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeemGreen)
            ) {
                Text(text = buttonText, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
