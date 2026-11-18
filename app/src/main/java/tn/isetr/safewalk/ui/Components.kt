package tn.isetr.safewalk.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Bouton principal : dégradé prune → mauve, ombre douce, effet d'appui. */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: (@Composable () -> Unit)? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "btnScale")
    val shape = RoundedCornerShape(20.dp)
    val brush: Brush = when {
        !enabled -> SolidColor(SafeColors.Plum.copy(alpha = 0.3f))
        pressed -> SolidColor(SafeColors.Plum)
        else -> SafeColors.PlumGradient
    }

    Box(
        modifier
            .scale(scale)
            .shadow(
                if (enabled) 8.dp else 0.dp, shape,
                ambientColor = SafeColors.Plum, spotColor = SafeColors.Plum
            )
            .clip(shape)
            .background(brush)
            .clickable(
                interactionSource = interaction, indication = null,
                enabled = enabled, role = Role.Button, onClick = onClick
            )
            .heightIn(min = 52.dp)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                icon()
                Spacer(Modifier.width(10.dp))
            }
            Text(text, color = SafeColors.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Carte blanche à ombre douce prune ; cliquable si onClick est fourni. */
@Composable
fun SafeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && onClick != null) 0.98f else 1f, label = "cardScale")

    Column(
        modifier
            .scale(scale)
            .shadow(
                6.dp, shape,
                ambientColor = SafeColors.Plum.copy(alpha = 0.25f),
                spotColor = SafeColors.Plum.copy(alpha = 0.25f)
            )
            .clip(shape)
            .background(SafeColors.White)
            .then(
                if (onClick != null)
                    Modifier.clickable(interactionSource = interaction, indication = null, onClick = onClick)
                else Modifier
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun safeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SafeColors.Plum,
    unfocusedBorderColor = SafeColors.Mauve.copy(alpha = 0.45f),
    focusedLabelColor = SafeColors.Plum,
    unfocusedLabelColor = SafeColors.TextSoft,
    cursorColor = SafeColors.Plum,
    focusedTextColor = SafeColors.Text,
    unfocusedTextColor = SafeColors.Text,
    focusedContainerColor = SafeColors.White,
    unfocusedContainerColor = SafeColors.White
)