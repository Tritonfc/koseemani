package com.example.koseemani.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.ServiceInfo
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ServiceCompat
import com.example.koseemani.MainActivity
import com.example.koseemani.broadcast.SOSBroadcastReceiver
import com.example.koseemani.data.remote.GoogleDriveHelper
import com.example.koseemani.notifications.NotificationsHelper
import com.example.koseemani.utils.SMSManager
import com.example.koseemani.utils.testContacts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

class SendSmsService : Service() {
    private lateinit var broadCastReceiver: BroadcastReceiver
    private lateinit var videoRecordService: VideoRecordService
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val _locationFlow = MutableStateFlow<Location?>(null)
    private lateinit var locationCallback: LocationCallback
    private var currLocation = ""
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as VideoRecordService.LocalBinder
            videoRecordService = binder.getService()
//            mBound = true
            videoRecordService.getVideoFileForService = { filePath ->
                videoRecordService.stopForegroundService()
                coroutineScope.launch(Dispatchers.IO) {
                    uploadVideoAndSendSMS(filePath)
                }
                unbindService(this)
            }


        }

        override fun onServiceDisconnected(arg0: ComponentName) {
//            mBound = false


        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {

        broadCastReceiver = SOSBroadcastReceiver {
            Intent(this, VideoRecordService::class.java).also { intent ->
                bindService(intent, connection, 0)
            }
            startForegroundService(Intent(this, VideoRecordService::class.java))
        }

        setupLocationUpdates()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAsForegroundService()
        startLocationUpdates()
        registerReceiver(broadCastReceiver, IntentFilter("android.media.VOLUME_CHANGED_ACTION"))
        coroutineScope.launch {
            updateLocation()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        unregisterReceiver(broadCastReceiver)

        super.onDestroy()
    }

    fun stopService() {

        stopSelf()
    }

    private fun setupLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    _locationFlow.value = location
                }
            }
        }
    }

    /**
     * Starts the location updates using the FusedLocationProviderClient.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(
                LOCATION_UPDATES_INTERVAL_MS
            ).build(), locationCallback, Looper.getMainLooper()
        )
    }

    private suspend fun updateLocation(){
        _locationFlow.collectLatest { location ->
            currLocation = if (location == null) {
                "No last known location."
            } else {
                val geocoder = Geocoder(this, Locale.getDefault())
                val list: MutableList<Address>? =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                list?.get(0)?.getAddressLine(0) ?: "No location found"

            }
        }
    }


    private suspend fun uploadVideoAndSendSMS(filePath: String) {

        GoogleDriveHelper.uploadVideoToDrive(this@SendSmsService, filePath) { videoLink ->
            val firstMessagePart =
                "SOS,I am in danger and located at: $currLocation."
            val secondMessagePart = "Here's a clip of me:"
            val messagesPart = arrayListOf(
                firstMessagePart,
                secondMessagePart,
                videoLink

            )
            SMSManager.sendSOSMessage(
                messages = messagesPart,
                emergencyContacts = testContacts,
                )

        }
    }

    private fun startAsForegroundService() {
        // create the notification channel
        NotificationsHelper.createNotificationChannel(this)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val message = "Koseemani is listening for SOS alerts"

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            1,
            NotificationsHelper.buildNotification(this, message, intent),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            }
        )
    }

    companion object {
        private val LOCATION_UPDATES_INTERVAL_MS = 5.seconds.inWholeMilliseconds
    }

}
