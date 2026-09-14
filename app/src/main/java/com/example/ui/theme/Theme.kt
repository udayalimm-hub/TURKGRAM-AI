package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = TurkgramCoral,
    onPrimary = Color.White,
    primaryContainer = TurkgramCrimson,
    onPrimaryContainer = Color.White,
    secondary = TurkgramPink,
    onSecondary = Color.White,
    secondaryContainer = TurkgramPurple,
    onSecondaryContainer = Color.White,
    tertiary = TurkgramGold,
    onTertiary = Color.Black,
    background = TurkgramDarkBg,
    onBackground = TurkgramTextPrimary,
    surface = TurkgramDarkSurface,
    onSurface = TurkgramTextPrimary,
    surfaceVariant = TurkgramDarkSurfaceVariant,
    onSurfaceVariant = TurkgramTextSecondary,
    outline = TurkgramDarkBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = TurkgramRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEBEE),
    onPrimaryContainer = TurkgramCrimson,
    secondary = TurkgramPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),
    onSecondaryContainer = TurkgramViolet,
    tertiary = TurkgramGold,
    onTertiary = Color.Black,
    background = TurkgramLightBg,
    onBackground = Color(0xFF1E1E24),
    surface = TurkgramLightSurface,
    onSurface = Color(0xFF1E1E24),
    surfaceVariant = TurkgramLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF555B6E),
    outline = TurkgramLightBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use intentional Turkgram branding
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
