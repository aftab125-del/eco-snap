package com.ecosnap.bharat.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ecosnap.bharat.theme.*

@Composable
fun WasteCategoryIcon(
    category: String,
    modifier: Modifier = Modifier,
    containerSize: Dp = 44.dp,
    iconSize: Dp = 24.dp
) {
    val (vector, tint, bg) = resolveCategoryVisuals(category)

    Box(
        modifier = modifier
            .size(containerSize)
            .background(bg, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = vector,
            contentDescription = category,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun WasteCategoryIcon(
    category: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    WasteCategoryIcon(
        category = category,
        modifier = modifier,
        containerSize = size,
        iconSize = (size.value * 0.55f).dp
    )
}

private fun resolveCategoryVisuals(category: String): Triple<ImageVector, Color, Color> {
    val lower = category.lowercase()
    return when {
        lower.contains("plastic") || lower.contains("pet") || lower.contains("bottle") ->
            Triple(Icons.Filled.Recycling, NeemGreenDark, PaleMint)

        lower.contains("organic") || lower.contains("wet") || lower.contains("citrus") || lower.contains("fruit") || lower.contains("peel") ->
            Triple(Icons.Filled.Eco, Color(0xFF2E7D32), Color(0xFFE8F5E9))

        lower.contains("cardboard") || lower.contains("paper") || lower.contains("गत्ता") || lower.contains("अखबार") ->
            Triple(Icons.Filled.Inventory2, RupeeGold, RupeeGoldLight)

        lower.contains("metal") || lower.contains("iron") || lower.contains("copper") || lower.contains("लोहा") || lower.contains("तांबा") ->
            Triple(Icons.Filled.Build, Color(0xFF455A64), Color(0xFFECEFF1))

        lower.contains("battery") || lower.contains("e-waste") || lower.contains("बैटरी") ->
            Triple(Icons.Filled.BatteryChargingFull, Color(0xFFC62828), Color(0xFFFFEBEE))

        lower.contains("oil") || lower.contains("energy") || lower.contains("तेल") ->
            Triple(Icons.Filled.Bolt, SuryaAmber, SuryaAmberLight)

        else ->
            Triple(Icons.Filled.RestoreFromTrash, NeemGreenDark, PaleMint)
    }
}
