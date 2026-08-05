package com.svapravrithi.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.svapravrithi.app.R

/**
 * The design reference specifies Poppins (Google Font) as the typeface, with this scale:
 *   Heading Large  24sp Semibold
 *   Heading Medium 18sp Semibold
 *   Body Large     14sp Regular
 *   Body Medium    12sp Regular
 *   Caption        10sp Regular
 *
 * To use the real Poppins font: open this module in Android Studio, right-click
 * res/font -> New -> Font Resource, search "Poppins" in the Google Fonts tab, and
 * download Regular, Medium, and SemiBold weights. Android Studio will wire up the
 * font-family XML and certificates automatically. Until then, this falls back to
 * FontFamily.SansSerif so the app builds and looks correct out of the box.
 */
val PoppinsFallback = FontFamily.SansSerif

// Uncomment once Poppins .ttf files are added to res/font via Android Studio:
// val Poppins = FontFamily(
//     Font(R.font.poppins_regular, FontWeight.Normal),
//     Font(R.font.poppins_medium, FontWeight.Medium),
//     Font(R.font.poppins_semibold, FontWeight.SemiBold),
//     Font(R.font.poppins_bold, FontWeight.Bold),
// )

val AppFontFamily = PoppinsFallback

val SvaTypography = Typography(
    displayLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp, lineHeight = 30.sp), // Heading Large
    headlineMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp), // Heading Medium
    titleLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp), // Body Large
    bodyMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp), // Body Medium
    labelLarge = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = AppFontFamily, fontWeight = FontWeight.Normal, fontSize = 10.sp, lineHeight = 14.sp), // Caption
)
