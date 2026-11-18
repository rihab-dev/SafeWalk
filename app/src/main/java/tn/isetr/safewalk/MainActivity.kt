package tn.isetr.safewalk

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.isetr.safewalk.ui.ContactsScreen
import tn.isetr.safewalk.ui.GradientButton
import tn.isetr.safewalk.ui.HomeScreen
import tn.isetr.safewalk.ui.MapView
import tn.isetr.safewalk.ui.SafeCard
import tn.isetr.safewalk.ui.SafeColors
import tn.isetr.safewalk.ui.SettingsScreen
import tn.isetr.safewalk.ui.SosButton
import tn.isetr.safewalk.ui.theme.SafeWalkTheme
import tn.isetr.safewalk.viewmodel.LocationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalConfiguration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { SafeWalkTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen() {
    var tab by remember { mutableIntStateOf(0) }

    val items = listOf(
        "Accueil" to Icons.Default.Home,
        "Carte" to Icons.Default.Place,
        "Contacts" to Icons.Default.Person,
        "Paramètres" to Icons.Default.Settings
    )

    Scaffold(
        containerColor = SafeColors.Ivory,
        bottomBar = {
            NavigationBar(containerColor = SafeColors.White, tonalElevation = 8.dp) {
                items.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = tab == index,
                        onClick = { tab = index },
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SafeColors.Peach,
                            selectedIconColor = SafeColors.Plum,
                            selectedTextColor = SafeColors.Plum,
                            unselectedIconColor = SafeColors.TextSoft,
                            unselectedTextColor = SafeColors.TextSoft
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (tab) {
                0 -> HomeScreen(
                    onOpenMap = { tab = 1 },
                    onOpenContacts = { tab = 2 }
                )
                1 -> LocationScreen()
                2 -> ContactsScreen()
                else -> SettingsScreen()
            }
        }
    }
}

@Composable
fun LocationScreen(vm: LocationViewModel = viewModel()) {
    val location by vm.location.collectAsState()
    val history by vm.history.collectAsState()

    val locale = LocalConfiguration.current.locales[0]
    val formatter = remember(locale) { SimpleDateFormat("dd/MM HH:mm:ss", locale) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) vm.refreshLocation()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SafeColors.BackgroundGradient)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SafeCard(Modifier.fillMaxWidth()) {
            Text(
                text = location?.let { "Lat: ${it.latitude}\nLng: ${it.longitude}" }
                    ?: "Position inconnue",
                color = SafeColors.Text
            )
        }
        Spacer(Modifier.height(12.dp))
        GradientButton(
            text = "Obtenir ma position",
            onClick = {
                launcher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        SosButton()
        Spacer(Modifier.height(8.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Historique : ${history.size} point(s)",
                fontWeight = FontWeight.SemiBold,
                color = SafeColors.Plum
            )
            TextButton(onClick = { vm.clearHistory() }) {
                Text("Effacer", color = SafeColors.Coral)
            }
        }
        LazyColumn(Modifier.fillMaxWidth().height(80.dp)) {
            items(history) { p ->
                val heure = formatter.format(Date(p.timestamp))
                Text(
                    "$heure  ${"%.5f".format(p.latitude)}, ${"%.5f".format(p.longitude)}",
                    color = SafeColors.TextSoft
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        MapView(
            location = location,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
        )
    }
}