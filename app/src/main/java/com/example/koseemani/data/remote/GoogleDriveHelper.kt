package com.example.koseemani.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import com.example.koseemani.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Collections

object GoogleDriveHelper {


    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
//            .requestIdToken(getString(context,R.string.CLIENT_ID))

            .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE))
            .build()

        return GoogleSignIn.getClient(context, signInOptions)
    }

    fun getSignedInFromAccount(intent: Intent?): Task<GoogleSignInAccount> =
        GoogleSignIn.getSignedInAccountFromIntent(intent)


    fun getDriveBuilder(context: Context): Drive {
        lateinit var credential: GoogleAccountCredential

        GoogleSignIn.getLastSignedInAccount(context)?.let { googleAccount ->

            // get credentials
            credential = GoogleAccountCredential.usingOAuth2(
                context, listOf(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE)
            )
            credential.selectedAccount = googleAccount.account!!

            // get Drive Instance

        }
        return Drive
            .Builder(
                NetHttpTransport(),
                GsonFactory(),
                credential
            )
            .setApplicationName(context.getString(R.string.app_name))
            .build()

    }

    fun checkIfFileExists(
        name: String,
        mimeType: String,
        driveService: Drive
    ): Pair<Boolean, String> {

        var pageToken: String? = null

        do {

            val result: FileList =
                driveService
                    .files()
                    .list()
                    .setQ("mimeType='$mimeType'")
                    .setSpaces("drive")
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute()

            for (file in result.files) {

//                Log.d(TAG_UPLOAD_FILE , "Found file: %s (%s)\n ${file.name}, ${file.id} ")
                if (name == file.name) return Pair(true, file.id)
            }

            pageToken = result.nextPageToken

        } while (pageToken != null)

        return Pair(false, "null")
    }

    suspend fun uploadVideoToDrive(
        context: Context, videoFilePath: String, onCompletedUpload: (String) -> Unit
    ) {
        var videoLink: String? = null

        val gFolder = File()
        // Set file name and MIME
        gFolder.name = "Koseemani Videos"
        gFolder.mimeType = "application/vnd.google-apps.folder"
        val driveService = getDriveBuilder(context)

        val filePair =
           checkIfFileExists(gFolder.name, gFolder.mimeType, driveService)

        val fileId: String =
            if (filePair.first) filePair.second else driveService.Files().create(gFolder)
                .setFields("id").execute().id

        val metadata = File()
        metadata.name = "${System.currentTimeMillis()}"
        metadata.parents = Collections.singletonList(fileId)
        metadata.permissions = listOf(

        )

        val filePath = java.io.File(videoFilePath)

        val mediaContent = FileContent("video/mp4", filePath)
        try {

            videoLink =
                driveService.Files().create(metadata, mediaContent).setFields("id,webViewLink")
                    .execute().webViewLink



            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(context, "Sending alert to contacts", Toast.LENGTH_LONG).show()
            }
        } catch (e: IOException) {
            Log.e("UPLOAD ERROR", e.message!!)
            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(context, "Sending alerts to contacts Failed", Toast.LENGTH_LONG)
                    .show()
            }
        } finally {

            onCompletedUpload(videoLink ?: "")
        }


    }
}
