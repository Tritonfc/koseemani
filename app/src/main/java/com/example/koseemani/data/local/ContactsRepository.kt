package com.example.koseemani.data.local

import com.example.koseemani.data.KoseemaniRepository
import kotlinx.coroutines.flow.Flow

class ContactsRepository(private val contactDao: ContactDao):KoseemaniRepository {
    override fun fetchAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts()

    override suspend fun addContact(contact: Contact) = contactDao.insert(contact)

    override suspend fun deleteContact(contact: Contact) = contactDao.delete(contact)
}