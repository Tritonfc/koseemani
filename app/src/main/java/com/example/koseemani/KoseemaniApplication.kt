package com.example.koseemani

import android.app.Application
import com.example.koseemani.utils.SMSManager

class KoseemaniApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        SMSManager.application = this.applicationContext as Application
    }

}