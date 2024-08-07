package com.example.koseemani.di

import androidx.lifecycle.viewmodel.CreationExtras
import com.example.koseemani.KoseemaniApplication
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.koseemani.ui.contacts.ContactsViewModel

object KoseeViewmodelProvider {
    val  viewModelFactory = viewModelFactory {
        // Other Initializers
        // Initializer for ItemEntryViewModel
        initializer {
            ContactsViewModel(koseeApplication().container.contactRepo)
        }
        //...
    }
    fun CreationExtras.koseeApplication(): KoseemaniApplication =
        (this[AndroidViewModelFactory.APPLICATION_KEY] as KoseemaniApplication)

}