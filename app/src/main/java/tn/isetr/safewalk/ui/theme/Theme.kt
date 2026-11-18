package tn.isetr.safewalk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import tn.isetr.safewalk.ui.SafeColors

private val PlumCoralScheme = lightColorScheme(
    primary = SafeColors.Plum,
    onPrimary = SafeColors.White,
    primaryContainer = SafeColors.Peach,
    onPrimaryContainer = SafeColors.Plum,
    secondary = SafeColors.Coral,
    onSecondary = SafeColors.White,
    secondaryContainer = SafeColors.Peach,
    onSecondaryContainer = SafeColors.Plum,
    tertiary = SafeColors.Mauve,
    background = SafeColors.Ivory,
    onBackground = SafeColors.Text,
    surface = SafeColors.White,
    onSurface = SafeColors.Text,
    outline = SafeColors.Mauve
)

@Composable
fun SafeWalkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PlumCoralScheme,
        typography = Typography,
        content = content
    )
}