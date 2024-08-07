package com.example.koseemani.ui.contacts

import com.example.koseemani.data.local.Contact

data class ContactsUiState(
    val contacts : List<Contact> = listOf()
)
