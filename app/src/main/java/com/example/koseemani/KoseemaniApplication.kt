package com.example.koseemani

import android.app.Application
import com.example.koseemani.di.AppContainer
import com.example.koseemani.di.KoseeAppContainer
import com.example.koseemani.utils.SMSManager

class KoseemaniApplication:Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = KoseeAppContainer(this)
        SMSManager.application = this.applicationContext as Application
    }

}