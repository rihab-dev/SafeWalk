package tn.isetr.safewalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.isetr.safewalk.viewmodel.ContactViewModel

@Composable
fun ContactsScreen(vm: ContactViewModel = viewModel()) {
    val contacts by vm.contacts.collectAsState()
    var nom by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(SafeColors.BackgroundGradient)
            .padding(20.dp)
    ) {
        Text(
            "Contacts d'urgence",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SafeColors.Plum
        )
        Spacer(Modifier.height(14.dp))

        SafeCard(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                singleLine = true,
                colors = safeFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = numero,
                onValueChange = { numero = it },
                label = { Text("Numéro de téléphone") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = safeFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            GradientButton(
                text = "Ajouter",
                onClick = {
                    vm.addContact(nom, numero)
                    nom = ""
                    numero = ""
                },
                enabled = nom.isNotBlank() && numero.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(16.dp))

        if (contacts.isEmpty()) {
            Text("Aucun contact enregistré.", color = SafeColors.TextSoft)
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(contacts) { c ->
                SafeCard(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(44.dp).clip(CircleShape).background(SafeColors.CoralGradient),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                c.nom.firstOrNull()?.uppercase() ?: "?",
                                color = SafeColors.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.nom, fontWeight = FontWeight.Bold, color = SafeColors.Plum)
                            Text(c.numero, fontSize = 13.sp, color = SafeColors.TextSoft)
                        }
                        TextButton(onClick = { vm.deleteContact(c) }) {
                            Text("Supprimer", color = SafeColors.Coral)
                        }
                    }
                }
            }
        }
    }
}