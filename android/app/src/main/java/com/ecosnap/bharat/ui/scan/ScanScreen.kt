package com.ecosnap.bharat.ui.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ecosnap.bharat.data.AiAnalysisOutput
import com.ecosnap.bharat.data.AiWasteClassifier
import com.ecosnap.bharat.data.AppLanguage
import com.ecosnap.bharat.data.DataRepository
import com.ecosnap.bharat.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt as OutlinedCameraAlt
import com.ecosnap.bharat.ui.common.WasteCategoryIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(
    repository: DataRepository,
    onBack: () -> Unit,
    onPhotoCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentLanguage by repository.appLanguage.collectAsState()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var scanStatusText by remember { mutableStateOf("") }
    var detectedResult by remember { mutableStateOf<AiAnalysisOutput?>(null) }
    var permissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    // Native Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            isScanning = true
            permissionDeniedMessage = null

            scope.launch {
                scanStatusText = if (currentLanguage == AppLanguage.ENGLISH) "Scanning image contours..." else "वस्तु की आकृति स्कैन हो रही है..."
                delay(600)
                scanStatusText = if (currentLanguage == AppLanguage.ENGLISH) "AI analyzing material density & recyclability..." else "AI सामग्री व रीसाइक्लिंग श्रेणी जांच रहा है..."
                delay(700)
                val analysis = AiWasteClassifier.analyzeImage(bitmap)
                detectedResult = analysis
                scanStatusText = if (currentLanguage == AppLanguage.ENGLISH) "Identified: ${analysis.item.itemNameEnglish} (${analysis.confidenceScore}%)" else "पहचान: ${analysis.item.itemNameHindi} (${analysis.confidenceScore}%)"
                repository.registerDetectedItem(analysis.item, analysis.solutionBundle)
                delay(900)
                isScanning = false
                onPhotoCaptured(analysis.item.id)
            }
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val stream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(stream)
                stream?.close()
                if (bitmap != null) {
                    capturedBitmap = bitmap
                    isScanning = true
                    scope.launch {
                        scanStatusText = if (currentLanguage == AppLanguage.ENGLISH) "Analyzing photo from gallery..." else "गैलरी फोटो की जांच हो रही है..."
                        delay(700)
                        val analysis = AiWasteClassifier.analyzeImage(bitmap)
                        detectedResult = analysis
                        scanStatusText = if (currentLanguage == AppLanguage.ENGLISH) "Identified: ${analysis.item.itemNameEnglish}" else "पहचान: ${analysis.item.itemNameHindi}"
                        repository.registerDetectedItem(analysis.item, analysis.solutionBundle)
                        delay(800)
                        isScanning = false
                        onPhotoCaptured(analysis.item.id)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            permissionDeniedMessage = null
            cameraLauncher.launch(null)
        } else {
            permissionDeniedMessage = if (currentLanguage == AppLanguage.ENGLISH)
                "Camera permission required to scan waste. You can also pick from gallery."
            else
                "कचरा स्कैन करने के लिए कैमरा अनुमति आवश्यक है। आप गैलरी से भी फोटो चुन सकते हैं।"
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            cameraLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Infinite animation for scanning laser line
    val infiniteTransition = rememberInfiniteTransition(label = "scannerLine")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPosition"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F0E))
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar with Back and 1-Tap Language Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = NeemGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = if (currentLanguage == AppLanguage.ENGLISH) "AI Waste Scanner" else "AI कचरा स्कैनर",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF222624),
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
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "EN" else "हि",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        if (permissionDeniedMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A1818)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color(0xFFFFCDD2),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = permissionDeniedMessage ?: "",
                        color = Color(0xFFFFCDD2),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Center Viewfinder Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(2.dp, if (isScanning) SuryaAmber else NeemGreen, RoundedCornerShape(24.dp))
                .background(Color(0xFF141716), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (capturedBitmap != null) {
                Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = "Captured waste image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Point camera at any waste item" else "कचरे की ओर कैमरा करें",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH)
                            "AI identifies material, suggests Jugaad & calculates live scrap value"
                        else
                            "AI सामग्री पहचानेगा, जुगाड़ बताएगा और कबाड़ी भाव आंकेगा",
                        color = Color(0xFF9E9E9E),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }

            // Scanning Laser Line Overlay
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.015f)
                            .align(Alignment.TopCenter)
                            .offset(y = (laserOffset * 300).dp)
                            .background(SuryaAmber, RoundedCornerShape(2.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scanning Status Indicator
        if (isScanning) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF242718),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = SuryaAmber,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = scanStatusText,
                        color = SuryaAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Primary Action Controls (Camera Shutter & Gallery)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pick from Gallery Button
            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Gallery" else "गैलरी",
                        fontSize = 13.sp
                    )
                }
            }

            // Big Camera Shutter Button
            Button(
                onClick = { launchCamera() },
                modifier = Modifier.size(76.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = NeemGreen),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Capture",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Quick Demo Simulation Button
            OutlinedButton(
                onClick = {
                    val analysis = AiWasteClassifier.analyzeImage(null)
                    repository.registerDetectedItem(analysis.item, analysis.solutionBundle)
                    onPhotoCaptured(analysis.item.id)
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.ENGLISH) "Test Demo" else "टेस्ट डेमो",
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Items Quick Strip
        Text(
            text = if (currentLanguage == AppLanguage.ENGLISH) "Or pick a common Indian waste item:" else "या इनमें से किसी वस्तु को चुनें:",
            color = Color(0xFFAAAAAA),
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repository.sampleItems.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E2220),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onPhotoCaptured(item.id) }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WasteCategoryIcon(category = item.materialCategory, containerSize = 34.dp, iconSize = 18.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (currentLanguage == AppLanguage.ENGLISH) item.itemNameEnglish else item.itemNameHindi,
                            color = Color.White,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
