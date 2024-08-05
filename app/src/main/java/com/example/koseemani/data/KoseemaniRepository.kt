package com.example.koseemani.data

import android.content.Context

interface KoseemaniRepository {
    fun uploadDataToDrive(context: Context, videoFilePath: String, onCompletedUpload: (String) -> Unit)


}