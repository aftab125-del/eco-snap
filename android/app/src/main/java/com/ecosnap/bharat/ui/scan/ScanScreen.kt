package com.ecosnap.bharat.ui.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import com.ecosnap.bharat.ui.common.LanguageSelectionDialog
import com.ecosnap.bharat.ui.common.LanguageSelectorButton

@Composable
fun ScanScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onPhotoCaptured: (String) -> Unit
) {
    val currentLanguage by repository.appLanguage.collectAsState()
    var selectedItemId by remember { mutableStateOf(repository.sampleItems.first().id) }
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
            .background(Color.Black)
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Text(text = "✕", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = if (currentLanguage == AppLanguage.ENGLISH) "📸 Waste Scanner" else "📸 कचरा स्कैनर",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            LanguageSelectorButton(
                currentLanguage = currentLanguage,
                onClick = { showLanguageDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Viewfinder Frame
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(2.dp, NeemGreen, RoundedCornerShape(20.dp))
                .background(Color(0xFF121413), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val currentItem = repository.sampleItems.find { it.id == selectedItemId }
                Text(text = currentItem?.iconEmoji ?: "🔍", fontSize = 72.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "Place object in the center of frame" else "वस्तु को फ्रेम के बीच में रखें",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "AI will analyze material & condition" else "AI सामग्री और स्थिति की जांच करेगा",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Select waste to simulate for testing
        Text(
            text = if (currentLanguage == AppLanguage.ENGLISH) "Select demo item to test:" else "डेमो आइटम चुनें (Select item to test):",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repository.sampleItems.forEach { item ->
                val isSelected = item.id == selectedItemId
                val name = item.getItemName(currentLanguage)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) NeemGreen else Color(0xFF232725),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedItemId = item.id }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = item.iconEmoji, fontSize = 20.sp)
                        Text(
                            text = if (name.length > 8) name.take(8) + ".." else name,
                            color = Color.White,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large Shutter Snap Button
        Button(
            onClick = { onPhotoCaptured(selectedItemId) },
            modifier = Modifier.size(76.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = NeemGreen),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .border(3.dp, Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (currentLanguage == AppLanguage.ENGLISH) "Tap shutter to capture photo" else "फोटो खींचने के लिए दबाएं",
            color = Color.White,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}
