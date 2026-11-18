package tn.isetr.safewalk.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object SafeColors {
    val Plum = Color(0xFF51265F)        // actions principales
    val Mauve = Color(0xFF9865B5)       // transitions, bordures, états secondaires
    val Coral = Color(0xFFFF7867)       // accents / highlights
    val Peach = Color(0xFFFFD2B8)       // fonds secondaires
    val Ivory = Color(0xFFFFF7F2)       // fond d'écran
    val Text = Color(0xFF302438)
    val TextSoft = Color(0xFF302438).copy(alpha = 0.6f)
    val White = Color(0xFFFFFFFF)
    val Emergency = Color(0xFFD93636)   // UNIQUEMENT pour le SOS

    val PlumGradient = Brush.linearGradient(listOf(Plum, Mauve))
    val CoralGradient = Brush.linearGradient(listOf(Coral, Peach))
    val BackgroundGradient = Brush.verticalGradient(listOf(Ivory, Peach.copy(alpha = 0.35f)))
    val SosGradient = Brush.verticalGradient(listOf(Color(0xFFE85A5A), Emergency))
}