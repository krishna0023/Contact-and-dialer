package com.miuidialer.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MIUIGreen = Color(0xFF4CAF50)
val MIUIGreenDark = Color(0xFF388E3C)
val MIUIRed = Color(0xFFF44336)
val MIUIBlue = Color(0xFF2196F3)
val MIUIGray = Color(0xFF9E9E9E)
val MIUILightGray = Color(0xFFF5F5F5)
val MIUIDivider = Color(0xFFE0E0E0)
val MIUITextPrimary = Color(0xFF212121)
val MIUITextSecondary = Color(0xFF757575)
val MIUICallBgStart = Color(0xFF1A0533)
val MIUICallBgEnd = Color(0xFF3D1A6B)

private val LightColorScheme = lightColorScheme(
    primary = MIUIGreen,
    onPrimary = Color.White,
    secondary = MIUIBlue,
    background = Color.White,
    surface = Color.White,
    onBackground = MIUITextPrimary,
    onSurface = MIUITextPrimary
)

@Composable
fun MIUIDialerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
