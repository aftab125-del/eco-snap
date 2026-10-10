package com.ecosnap.bharat.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*

@Composable
fun AuthScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    var isLoginTab by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("hi") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KhadiBackground)
            .statusBarsPadding()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back Row
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
            Text(
                text = "मेरा खाता (My Account)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DeepCharcoal
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // App Logo & Greeting
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(color = PaleMint, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = "App Logo",
                tint = NeemGreenDark,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = "EcoSnap Bharat",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = NeemGreenDark
        )
        Text(
            text = "खाता बनाएं और अपनी बचत व इतिहास सुरक्षित रखें",
            fontSize = 13.sp,
            color = SlateGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Switcher (Login vs Register)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = PaleMint,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isLoginTab) NeemGreen else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isLoginTab = true; errorMessage = null }
                ) {
                    Text(
                        text = "लॉग इन (Login)",
                        color = if (isLoginTab) Color.White else DeepCharcoal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (!isLoginTab) NeemGreen else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isLoginTab = false; errorMessage = null }
                ) {
                    Text(
                        text = "नया खाता (Register)",
                        color = if (!isLoginTab) Color.White else DeepCharcoal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 10.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Error Banner
        if (errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = TerracottaRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = errorMessage ?: "",
                        color = TerracottaRed,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Form Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!isLoginTab) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("आपका नाम (Full Name)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Language Preference
                    Text(text = "पसंदीदा भाषा (Language):", fontSize = 12.sp, color = SlateGray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("hi" to "हिन्दी", "hinglish" to "Hinglish", "en" to "English").forEach { (code, label) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedLang == code) NeemGreen else PaleMint,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedLang = code }
                            ) {
                                Text(
                                    text = label,
                                    color = if (selectedLang == code) Color.White else DeepCharcoal,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("ईमेल (Email Address)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("पासवर्ड (Password)") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        if (isLoginTab) {
                            repository.login(email, password) { success, err ->
                                isLoading = false
                                if (success) onAuthSuccess() else errorMessage = err
                            }
                        } else {
                            repository.signup(name, email, password, selectedLang) { success, err ->
                                isLoading = false
                                if (success) onAuthSuccess() else errorMessage = err
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeemGreen)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isLoginTab) "लॉग इन करें" else "खाता बनाएं",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Guest Mode Option
        TextButton(onClick = onBack) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "बिना लॉगिन के जारी रखें (Continue as Guest)",
                    color = SlateGray,
                    fontSize = 13.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = SlateGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
