package com.example.koseemani.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Camera
import android.hardware.Camera.Size
import android.media.MediaRecorder
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ServiceCompat
import com.example.koseemani.MainActivity
import com.example.koseemani.notifications.NotificationsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

private const val TAG = "VIDEO RECORD ERROR:"

class VideoRecordService : Service() {
    private val binder = LocalBinder()

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
    var isFromSleepMode = true


    private var mSurfaceView: SurfaceView? = null
    private var mSurfaceHolder: SurfaceHolder? = null
    var mServiceCamera: Camera? = null
    private var mRecordingStatus = false
    private var mMediaRecorder: MediaRecorder? = null

    var videoSavePathInDevice: String = ""
    private val VIDEO_RECORDER_FILE_EXT_MP4 = ".mp4"
    private val VIDEO_RECORDER_FOLDER = "AudioRecorder"
    private val currentFormat = 0
    private val file_exts = arrayOf(VIDEO_RECORDER_FILE_EXT_MP4)

    var getVideoFile: ((Uri) -> Unit)? = null
    var stopService: (() -> Unit)? = null
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): VideoRecordService = this@VideoRecordService
    }

    override fun onCreate() {

        mRecordingStatus = false
        mServiceCamera = MainActivity.camera
        mSurfaceView = MainActivity.surfaceView
        mSurfaceHolder = MainActivity.surfaceHolder


//        Log.e(TAG, "ts_: $format")
////        videoSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + format + "videoRecording.mp4";
        //        videoSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + format + "videoRecording.mp4";
        videoSavePathInDevice = getFilename()

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (MainActivity.camera != null) {

            mServiceCamera = MainActivity.camera
            mSurfaceView = MainActivity.surfaceView
            mSurfaceHolder = MainActivity.surfaceHolder
            startAsForegroundService()
            performServiceTasks()


        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
//        mRecordingStatus = false
//        Toast.makeText(this, "I have been destroyed", Toast.LENGTH_SHORT).show()
    }

    fun stopForegroundService() {
//        coroutineScope.cancel()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun performServiceTasks() {
        coroutineScope.launch {

            startRecording()




            delay(6000)

            stopRecording()


            getVideoFile?.invoke(getVideoFileUri(videoSavePathInDevice))
            stopService?.invoke()

        }
    }

    suspend fun startRecording() {
        try {
            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(baseContext, "Recording Started", Toast.LENGTH_SHORT).show()
            }

//            mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
            //mServiceCamera = camera;

            try {
                val p = mServiceCamera?.getParameters()
                val listPreviewSize: List<Camera.Size> = p?.supportedPreviewSizes ?: emptyList()
                val previewSize: Camera.Size = listPreviewSize[0] as Size
                p?.setPreviewSize(previewSize.width, previewSize.height)
                mServiceCamera?.setParameters(p)
            } catch (e: RuntimeException) {
                return@startRecording
            }

            try {
                mServiceCamera?.setPreviewDisplay(mSurfaceHolder)
                mServiceCamera?.startPreview()
            } catch (e: IOException) {
                Log.e(TAG, Objects.requireNonNull(e.message!!))
                e.printStackTrace()
            }
            try {
                mServiceCamera?.unlock()
            } catch (e: RuntimeException) {
                return
            }

            mMediaRecorder = MediaRecorder()
            mMediaRecorder?.setCamera(mServiceCamera)
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            mMediaRecorder?.setOutputFile(videoSavePathInDevice)
            //            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/"+format+".mp4");
//            mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+"recordVideo"+"/"+format+".mp4");
            mMediaRecorder?.setPreviewDisplay(mSurfaceHolder!!.surface)
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
            mRecordingStatus = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d(TAG, e.message!!)
            e.printStackTrace()
        }
    }

    suspend fun stopRecording() {
        if (null != mMediaRecorder) {
            withContext(Dispatchers.Main.immediate) {
                Toast.makeText(baseContext, "Recording Stopped", Toast.LENGTH_SHORT).show()
            }
            try {
                mServiceCamera?.reconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                mMediaRecorder?.stop()
            } catch (stopException: RuntimeException) {
                Log.e(TAG, stopException.message ?: "")
                return
            }
            try {
                mMediaRecorder?.reset()
            } catch (_: IllegalStateException) {
                return
            }


            //mServiceCamera.stopPreview();
            mMediaRecorder?.release()
//            mServiceCamera?.release()
            mServiceCamera = null
        }
    }

    private fun getVideoFileUri(videoPathString: String): Uri {
        return Uri.fromFile(File(videoPathString))
    }

    private fun getFilename(): String {

//                val simpleDateFormat = SimpleDateFormat("ddMMyyyyhhmm")
//        val format: String = simpleDateFormat.format(Date())
        val filepath = (Environment.getExternalStorageDirectory()
            .toString() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "MY_VIDEO_OK.mp4")
        val file = File(filepath, VIDEO_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + file_exts[currentFormat]
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