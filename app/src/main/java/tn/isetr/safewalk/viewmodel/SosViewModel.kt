package tn.isetr.safewalk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tn.isetr.safewalk.data.AppDatabase
import tn.isetr.safewalk.data.LocationRepository
import tn.isetr.safewalk.data.SettingsRepository
import tn.isetr.safewalk.data.SmsSender

sealed interface SosState {
    data object Idle : SosState
    data object Sending : SosState
    data class Sent(val count: Int) : SosState
    data class Error(val message: String) : SosState
}

class SosViewModel(private val app: Application) : AndroidViewModel(app) {

    private val locationRepo = LocationRepository(app)
    private val contactDao = AppDatabase.getInstance(app).contactDao()
    private val settings = SettingsRepository(app)

    private val _state = MutableStateFlow<SosState>(SosState.Idle)
    val state: StateFlow<SosState> = _state

    fun sendSos() {
        viewModelScope.launch {
            _state.value = SosState.Sending
            try {
                val contacts = contactDao.getAll().first()
                if (contacts.isEmpty()) {
                    _state.value = SosState.Error("Aucun contact d'urgence enregistré.")
                    return@launch
                }

                val loc = locationRepo.getCurrentLocation()
                val texte = if (loc != null) {
                    "${settings.message}\nhttps://maps.google.com/?q=${loc.latitude},${loc.longitude}"
                } else {
                    "${settings.message}\n(position indisponible)"
                }

                contacts.forEach { SmsSender.send(app, it.numero, texte) }
                _state.value = SosState.Sent(contacts.size)
            } catch (e: Exception) {
                _state.value = SosState.Error("Échec de l'envoi : ${e.message}")
            }
        }
    }

    fun reset() { _state.value = SosState.Idle }
}