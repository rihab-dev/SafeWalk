package tn.isetr.safewalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tn.isetr.safewalk.data.SettingsRepository

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val settings = remember { SettingsRepository(context) }
    var message by remember { mutableStateOf(settings.message) }
    var confirm by remember { mutableStateOf(settings.confirmBeforeSend) }
    var saved by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(SafeColors.BackgroundGradient)
            .padding(20.dp)
    ) {
        Text(
            "Paramètres",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SafeColors.Plum
        )
        Spacer(Modifier.height(16.dp))

        SafeCard(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it; saved = false },
                label = { Text("Message d'urgence") },
                colors = safeFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Les coordonnées GPS sont ajoutées automatiquement.",
                fontSize = 13.sp,
                color = SafeColors.TextSoft
            )
        }

        Spacer(Modifier.height(16.dp))
        SafeCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Confirmer avant d'envoyer le SOS",
                    Modifier.weight(1f),
                    color = SafeColors.Text
                )
                Switch(
                    checked = confirm,
                    onCheckedChange = {
                        confirm = it
                        settings.confirmBeforeSend = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SafeColors.White,
                        checkedTrackColor = SafeColors.Plum,
                        uncheckedThumbColor = SafeColors.Mauve,
                        uncheckedTrackColor = SafeColors.Peach,
                        uncheckedBorderColor = SafeColors.Mauve
                    )
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientButton(
                text = "Enregistrer",
                onClick = { settings.message = message.trim(); saved = true },
                enabled = message.isNotBlank()
            )
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = {
                message = SettingsRepository.DEFAULT_MSG
                settings.message = message
                saved = true
            }) { Text("Réinitialiser", color = SafeColors.Plum) }
        }
        if (saved) {
            Text(
                "Enregistré ✔",
                Modifier.padding(top = 10.dp),
                color = SafeColors.Coral,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}