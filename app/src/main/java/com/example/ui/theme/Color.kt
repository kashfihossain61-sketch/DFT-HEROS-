package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// LocalThemeDark is a CompositionLocal that provides the current theme state (dark/light)
val LocalThemeDark = staticCompositionLocalOf { true }

// Navy Dark Blue Palette
val NavyDeep: Color @Composable get() = if (LocalThemeDark.current) Color(0xFF050A18) else Color(0xFFF8FAFC)
val NavySurface: Color @Composable get() = if (LocalThemeDark.current) Color(0xFF0F172A) else Color(0xFFFFFFFF)
val NavySurfaceLight: Color @Composable get() = if (LocalThemeDark.current) Color(0xFF1E293B) else Color(0xFFF1F5F9)

// Accent Colors
val CyberBlue = Color(0xFF2563EB)     // Royal/Cyber blue for primary actions
val NeonCyan: Color @Composable get() = if (LocalThemeDark.current) Color(0xFF60A5FA) else Color(0xFF1D4ED8)
val GoldTrophy = Color(0xFFFFD700)    // Tertiary gold for points, wins, and champions

// Neutral Colors
val TextPrimary: Color @Composable get() = if (LocalThemeDark.current) Color(0xFFF1F5F9) else Color(0xFF0F172A)
val TextSecondary: Color @Composable get() = if (LocalThemeDark.current) Color(0xFF94A3B8) else Color(0xFF64748B)
val BorderColor: Color @Composable get() = if (LocalThemeDark.current) Color(0x1A60A5FA) else Color(0x262563EB)
val ChatBubbleSelf = Color(0xFF2563EB) // Royal blue for user messages
val ChatBubbleOther: Color @Composable get() = if (LocalThemeDark.current) Color(0xFF1E293B) else Color(0xFFF1F5F9)
