package com.example.koseemani.utils

import com.example.koseemani.data.local.Contact

fun Pair<Boolean,String>.returnBool():Boolean = this.first

fun List<Contact>.toNumberStringList():List<String> = this.map {contact->
    contact.phoneNumber.filterNot {char->
        char.isWhitespace()
    }
}.toList()