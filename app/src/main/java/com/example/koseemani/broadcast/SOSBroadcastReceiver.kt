package com.example.koseemani.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.koseemani.utils.SMSManager
import com.example.koseemani.utils.testContacts

class SOSBroadcastReceiver(val receiverCallback:()->Unit):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        receiverCallback()
//        SMSManager.sendSOSMessage(
//            "SOS, I am in danger. I am currently located at Alagbole street",
//            emergencyContacts = testContacts
//
//        )
    }
}