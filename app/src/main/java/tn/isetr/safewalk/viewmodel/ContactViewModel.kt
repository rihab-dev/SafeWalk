package tn.isetr.safewalk.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tn.isetr.safewalk.data.AppDatabase
import tn.isetr.safewalk.data.Contact

class ContactViewModel(app: Application) : AndroidViewModel(app) {

    private val dao = AppDatabase.getInstance(app).contactDao()

    val contacts: StateFlow<List<Contact>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addContact(nom: String, numero: String) {
        viewModelScope.launch {
            dao.insert(Contact(nom = nom.trim(), numero = numero.trim()))
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch { dao.delete(contact) }
    }
}