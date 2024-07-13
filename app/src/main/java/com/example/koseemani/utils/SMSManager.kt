package com.example.koseemani.utils

import android.app.Application
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService

object SMSManager {
    lateinit var application: Application


    fun sendSOSMessage(message: String, emergencyContacts: List<String>) {
        val smsManager = getSmsManagerInstance()
        try {
            emergencyContacts.forEach{phoneNumber->
                smsManager?.sendTextMessage(phoneNumber, null, message, null, null)

            }

        } catch (e: Exception) {
            Log.e("SMS_ERROR", e.toString())

            Toast.makeText(application, "$e",Toast.LENGTH_SHORT).show()

        }


    }


    private fun getSmsManagerInstance(): SmsManager? {
        return if (Build.VERSION.SDK_INT >= 23) {
            //if SDK is greater that or equal to 23 then
            //this is how we will initialize the SmsManager
            getSystemService(application, SmsManager::class.java)
        } else {
            //if user's SDK is less than 23 then
            //SmsManager will be initialized like this
            SmsManager.getDefault()
        }
    }
}