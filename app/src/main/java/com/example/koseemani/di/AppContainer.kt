package com.example.koseemani.di

import android.content.Context
import com.example.koseemani.data.KoseemaniRepository
import com.example.koseemani.data.local.ContactsDatabase
import com.example.koseemani.data.local.ContactsRepository

interface AppContainer {
   val contactDatabase: ContactsDatabase
   val contactRepo : KoseemaniRepository

}

class KoseeAppContainer(private val context: Context):AppContainer{

    override val contactDatabase: ContactsDatabase
        get() = ContactsDatabase.getDatabase(context)

    override val contactRepo: KoseemaniRepository by lazy{
        ContactsRepository(contactDatabase.contactDao())
    }

}