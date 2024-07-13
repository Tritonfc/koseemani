package com.example.koseemani.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ServiceCompat
import com.example.koseemani.notifications.NotificationsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoRecordService : Service() {
    private val binder = LocalBinder()

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    var getVideoFile: ((String) -> Unit)? = null
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): VideoRecordService = this@VideoRecordService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAsForegroundService()

        coroutineScope.launch {
            withContext(Dispatchers.Main.immediate){
                Toast.makeText(this@VideoRecordService, "Started RECORDING", Toast.LENGTH_SHORT).show()
            }



            delay(6000)
            withContext(Dispatchers.Main.immediate){
                Toast.makeText(this@VideoRecordService, "Stoppped RECORDING", Toast.LENGTH_SHORT).show()
            }

            getVideoFile?.invoke("Video file")
            stopForeground(STOP_FOREGROUND_REMOVE)
        }


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    fun stopForegroundService() {
        stopSelf()
    }
    private fun startAsForegroundService() {
        // create the notification channel
        NotificationsHelper.createNotificationChannel(this)

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.buildNotification(this),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            } else {
                0
            }
        )
    }


}