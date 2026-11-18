package tn.isetr.safewalk.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tn.isetr.safewalk.data.AppDatabase
import tn.isetr.safewalk.data.LocationRepository
import tn.isetr.safewalk.data.TrajetPoint

class LocationViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = LocationRepository(app)
    private val dao = AppDatabase.getInstance(app).trajetDao()

    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    val history: StateFlow<List<TrajetPoint>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshLocation() {
        viewModelScope.launch {
            val loc = repository.getCurrentLocation()
            _location.value = loc
            if (loc != null) {
                dao.insert(TrajetPoint(latitude = loc.latitude, longitude = loc.longitude))
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch { dao.clear() }
    }
}