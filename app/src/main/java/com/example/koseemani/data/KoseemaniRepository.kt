package com.example.koseemani.data

import android.content.Context
import com.example.koseemani.data.local.Contact
import com.example.koseemani.navigation.Contacts
import kotlinx.coroutines.flow.Flow

interface KoseemaniRepository {
   fun fetchAllContacts(): Flow<List<Contact>>

   suspend fun addContact(contact: Contact):Unit

   suspend fun deleteContact(contact: Contact):Unit





}