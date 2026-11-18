package tn.isetr.safewalk.ui

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.isetr.safewalk.data.SettingsRepository
import tn.isetr.safewalk.viewmodel.SosState
import tn.isetr.safewalk.viewmodel.SosViewModel

@Composable
fun SosButton(modifier: Modifier = Modifier, compact: Boolean = false, vm: SosViewModel = viewModel()) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()
    var showConfirm by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result[Manifest.permission.SEND_SMS] == true) vm.sendSos()
        else Toast.makeText(context, "Permission SMS refusée", Toast.LENGTH_SHORT).show()
    }

    fun startSos() = launcher.launch(
        arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val sending = state is SosState.Sending
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.94f else 1f, label = "sosScale")
    val shape = RoundedCornerShape(if (compact) 14.dp else 20.dp)

    Box(
        (if (compact) modifier.size(56.dp) else modifier.fillMaxWidth().height(56.dp))
            .scale(scale)
            .alpha(if (sending) 0.6f else 1f)
            .shadow(
                10.dp, shape,
                ambientColor = SafeColors.Emergency, spotColor = SafeColors.Emergency
            )
            .clip(shape)
            .background(SafeColors.SosGradient)
            .clickable(
                interactionSource = interaction, indication = null,
                enabled = !sending, role = Role.Button
            ) {
                if (SettingsRepository(context).confirmBeforeSend) showConfirm = true else startSos()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            if (sending && !compact) "Envoi..." else "SOS",
            color = SafeColors.White,
            fontWeight = FontWeight.Bold
        )
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            containerColor = SafeColors.White,
            shape = RoundedCornerShape(24.dp),
            title = { Text("Envoyer l'alerte SOS ?", color = SafeColors.Plum, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Un SMS avec votre position sera envoyé à vos contacts d'urgence.",
                    color = SafeColors.Text
                )
            },
            confirmButton = {
                TextButton(onClick = { showConfirm = false; startSos() }) {
                    Text("Envoyer", color = SafeColors.Emergency, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Annuler", color = SafeColors.Plum)
                }
            }
        )
    }

    LaunchedEffect(state) {
        when (val s = state) {
            is SosState.Sent ->
                Toast.makeText(context, "SOS envoyé à ${s.count} contact(s)", Toast.LENGTH_LONG).show()
            is SosState.Error ->
                Toast.makeText(context, s.message, Toast.LENGTH_LONG).show()
            else -> return@LaunchedEffect
        }
        vm.reset()
    }
}