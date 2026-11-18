package tn.isetr.safewalk.ui

import android.Manifest
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.isetr.safewalk.data.TrajetPoint
import tn.isetr.safewalk.viewmodel.ContactViewModel
import tn.isetr.safewalk.viewmodel.LocationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onOpenMap: () -> Unit,
    onOpenContacts: () -> Unit,
    locationVm: LocationViewModel = viewModel(),
    contactVm: ContactViewModel = viewModel()
) {
    val location by locationVm.location.collectAsState()
    val history by locationVm.history.collectAsState()
    val contacts by contactVm.contacts.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) locationVm.refreshLocation()
    }
    fun askLocation() = launcher.launch(
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(SafeColors.BackgroundGradient)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ----- En-tête -----
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Text(
                    greeting().uppercase(),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = SafeColors.TextSoft
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Un peu de\nsérénité.",
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SafeColors.Plum
                )
            }
            Box(
                Modifier
                    .size(44.dp)
                    .shadow(
                        6.dp, CircleShape,
                        ambientColor = SafeColors.Plum.copy(alpha = 0.25f),
                        spotColor = SafeColors.Plum.copy(alpha = 0.25f)
                    )
                    .clip(CircleShape)
                    .background(SafeColors.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = SafeColors.Plum
                )
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(8.dp)
                        .background(SafeColors.Coral, CircleShape)
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Surface(shape = RoundedCornerShape(50), color = SafeColors.Peach) {
            Text(
                "Votre sécurité en un coup d'œil",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SafeColors.Plum
            )
        }

        // ----- Carte + SOS -----
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(220.dp)
                .shadow(
                    10.dp, RoundedCornerShape(24.dp),
                    ambientColor = SafeColors.Plum.copy(alpha = 0.3f),
                    spotColor = SafeColors.Plum.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(24.dp))
        ) {
            MapView(location = location, modifier = Modifier.fillMaxSize())

            SosButton(
                compact = true,
                modifier = Modifier.align(Alignment.TopEnd).padding(12.dp)
            )

            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                shape = RoundedCornerShape(50),
                color = SafeColors.White,
                shadowElevation = 3.dp
            ) {
                Row(
                    Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(8.dp).background(
                            if (location != null) SafeColors.Coral else SafeColors.Mauve,
                            CircleShape
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (location != null) "Position prête" else "Position inconnue",
                        fontSize = 13.sp,
                        color = SafeColors.Text
                    )
                }
            }

            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .size(44.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(SafeColors.White)
                    .clickable { askLocation() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Obtenir ma position",
                    tint = SafeColors.Plum
                )
            }
        }

        // ----- Marche sécurisée -----
        Spacer(Modifier.height(16.dp))
        GradientButton(
            text = "Démarrer une marche sécurisée",
            onClick = { askLocation(); onOpenMap() },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            icon = { Icon(Icons.Default.Place, contentDescription = null, tint = SafeColors.White) }
        )
        Text(
            "Quelqu'un veille toujours sur vous.",
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            color = SafeColors.TextSoft
        )

        // ----- Cercle de confiance -----
        Spacer(Modifier.height(20.dp))
        SectionHeader("Votre cercle de confiance", "Gérer", onOpenContacts)
        Spacer(Modifier.height(10.dp))
        SafeCard(Modifier.fillMaxWidth(), onClick = onOpenContacts) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape).background(SafeColors.CoralGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = SafeColors.White)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        if (contacts.isEmpty()) "Aucun contact"
                        else contacts.take(2).joinToString(" & ") { it.nom },
                        fontWeight = FontWeight.Bold,
                        color = SafeColors.Plum
                    )
                    Text(
                        if (contacts.isEmpty()) "Ajoutez un contact de confiance"
                        else "Prêt(s) quand vous en avez besoin",
                        fontSize = 13.sp,
                        color = SafeColors.TextSoft
                    )
                }
            }
        }

        // ----- Trajet récent -----
        Spacer(Modifier.height(20.dp))
        SectionHeader("Trajet récent", "Voir tout", onOpenMap)
        Spacer(Modifier.height(10.dp))
        RecentJourneyCard(history)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SectionHeader(title: String, action: String, onAction: () -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            Modifier.weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SafeColors.Plum
        )
        Text(
            action,
            Modifier.clickable { onAction() },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = SafeColors.Coral
        )
    }
}

@Composable
private fun RecentJourneyCard(history: List<TrajetPoint>) {
    val locale = LocalConfiguration.current.locales[0]

    SafeCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                if (history.size < 2) {
                    Text("Aucun trajet enregistré", fontWeight = FontWeight.Bold, color = SafeColors.Plum)
                    Text(
                        "Appuyez sur « Obtenir ma position »",
                        fontSize = 13.sp,
                        color = SafeColors.TextSoft
                    )
                } else {
                    // history est trié du plus récent au plus ancien
                    val dernier = history.first()
                    val premier = history.last()
                    val km = totalDistanceKm(history)
                    val minutes = (dernier.timestamp - premier.timestamp) / 60_000
                    Text("Dernier trajet", fontWeight = FontWeight.Bold, color = SafeColors.Plum)
                    Text(
                        "${dateLabel(dernier.timestamp, locale)} · " +
                                String.format(locale, "%.1f km", km) +
                                " · $minutes min",
                        fontSize = 13.sp,
                        color = SafeColors.TextSoft
                    )
                }
            }
            if (history.size >= 2) {
                Surface(shape = RoundedCornerShape(50), color = SafeColors.Peach) {
                    Text(
                        "Terminé",
                        Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SafeColors.Plum
                    )
                }
            }
        }
    }
}

private fun greeting(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return if (h in 5..17) "Bonjour" else "Bonsoir"
}

private fun dateLabel(timestamp: Long, locale: Locale): String {
    val now = Calendar.getInstance()
    val d = Calendar.getInstance().apply { timeInMillis = timestamp }
    return if (now.get(Calendar.YEAR) == d.get(Calendar.YEAR) &&
        now.get(Calendar.DAY_OF_YEAR) == d.get(Calendar.DAY_OF_YEAR)
    ) "Aujourd'hui"
    else SimpleDateFormat("dd/MM", locale).format(Date(timestamp))
}

private fun totalDistanceKm(points: List<TrajetPoint>): Double {
    val r = FloatArray(1)
    var total = 0.0
    points.zipWithNext { a, b ->
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, r)
        total += r[0]
    }
    return total / 1000
}