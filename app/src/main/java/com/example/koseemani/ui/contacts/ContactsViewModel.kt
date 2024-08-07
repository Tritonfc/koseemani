package com.example.koseemani.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.koseemani.data.KoseemaniRepository
import com.example.koseemani.data.local.Contact
import com.example.koseemani.data.local.ContactsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContactsViewModel(private val contactsRepository: KoseemaniRepository) : ViewModel() {

    val homeUiState: StateFlow<ContactsUiState> =
        contactsRepository.fetchAllContacts().map {
            ContactsUiState(it)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ContactsUiState()
            )

    fun addNewContact(contact: Contact) {
        viewModelScope.launch {
            contactsRepository.addContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactsRepository.deleteContact(contact)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}