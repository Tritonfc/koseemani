package com.example.koseemani.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
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
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
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

    var startedBy = ""


    private lateinit var mSurfaceView: SurfaceView
    private lateinit var mSurfaceHolder: SurfaceHolder
    var mServiceCamera: Camera? = null
    private var mRecordingStatus = false
    private var mMediaRecorder: MediaRecorder? = null

    var videoSavePathInDevice: String = ""
    private val VIDEO_RECORDER_FILE_EXT_MP4 = ".mp4"
    private val VIDEO_RECORDER_FOLDER = "AudioRecorder"
    private val currentFormat = 0
    private val file_exts = arrayOf(VIDEO_RECORDER_FILE_EXT_MP4)

    var getVideoFile: ((String) -> Unit)? = null
    var getVideoFileForService: ((String) -> Unit)? = null
    var stopService: (() -> Unit)? = null
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): VideoRecordService = this@VideoRecordService
    }

    inner class SurfaceCallBack : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
//            mServiceCamera = Camera.open(0)
            startAsForegroundService()
            performServiceTasks()

        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
//            camera?.release()
            mServiceCamera?.release()

        }

    }

    override fun onCreate() {

        mRecordingStatus = false
        bindSurface()



        videoSavePathInDevice = getFilename()

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


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




            delay(10000)

            stopRecording()


            getVideoFile?.invoke(videoSavePathInDevice)
            getVideoFileForService?.invoke(videoSavePathInDevice)
            stopService?.invoke()
//            stopForegroundService()

        }
    }

    suspend fun startRecording() {
//        try {
        withContext(Dispatchers.Main.immediate) {
            Toast.makeText(baseContext, "Recording Started", Toast.LENGTH_SHORT).show()
        }

//            mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            mServiceCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
        //mServiceCamera = camera;

//            try {
//                val p = mServiceCamera?.getParameters()
//                val listPreviewSize: List<Camera.Size> = p?.supportedPreviewSizes ?: emptyList()
//                val previewSize: Camera.Size = listPreviewSize[0]
//                p?.setPreviewSize(1280, 720)
//                mServiceCamera?.setParameters(p)
//            } catch (e: RuntimeException) {
//                Log.e(TAG, Objects.requireNonNull(e.message!!))
//                return
//            }

//            try {
//                mServiceCamera?.setPreviewDisplay(mSurfaceHolder)
//                mServiceCamera?.startPreview()
//            } catch (e: IOException) {
//                Log.e(TAG, Objects.requireNonNull(e.message!!))
//                e.printStackTrace()
//            }

//            try {
//                mServiceCamera?.unlock()
//            } catch (e: RuntimeException) {
//                Log.e(TAG, Objects.requireNonNull(e.message!!))
//                return
//            }
        mServiceCamera?.apply {
            val p = parameters
            val listPreviewSize: List<Camera.Size> = p.supportedPreviewSizes
            val previewSize: Camera.Size = listPreviewSize[0]
            p.setPreviewSize(previewSize.width, previewSize.height)
            parameters = p
            setPreviewDisplay(mSurfaceHolder)
            startPreview()
            unlock()
        }
        mMediaRecorder = MediaRecorder()
        mMediaRecorder?.apply {
            setCamera(mServiceCamera)
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.CAMERA)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setOutputFile(videoSavePathInDevice)
//            mMediaRecorder?.setVideoFrameRate(16)
            setVideoEncodingBitRate(3000000)
            setPreviewDisplay(mSurfaceHolder.surface)
            prepare()
            start()
        }
//            mMediaRecorder?.setCamera(mServiceCamera)
//            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
//            mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)
//            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//            mMediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
//            mMediaRecorder?.setOutputFile(videoSavePathInDevice)
////            mMediaRecorder?.setVideoFrameRate(16)
//            mMediaRecorder?.setVideoEncodingBitRate(3000000)
//            mMediaRecorder?.setPreviewDisplay(mSurfaceHolder.surface)
//            mMediaRecorder?.prepare()
//            mMediaRecorder?.start()
        mRecordingStatus = true
//        } catch (e: IllegalStateException) {
//            e.printStackTrace()
//        } catch (e: IOException) {
//            Log.d(TAG, e.message!!)
//            e.printStackTrace()
//        }
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


            mServiceCamera?.stopPreview();
            mMediaRecorder?.release()
            mServiceCamera?.release()
            mServiceCamera = null
        }
    }

    private fun bindSurface() {

        mServiceCamera = Camera.open(0)
//        val linearLayout = LinearLayout(this)
//        linearLayout.layoutParams = LinearLayout.LayoutParams(
//            ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT
//        )
        val surfaceViewPrag = SurfaceView(this)
//        surfaceViewPrag.layoutParams = LinearLayout.LayoutParams(1, 1)
//        linearLayout.addView(surfaceViewPrag)


        mSurfaceView = surfaceViewPrag

        mSurfaceHolder = mSurfaceView.holder
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//           surfaceViewPrag.holder.addCallback(SurfaceCallBack())
        mSurfaceHolder.addCallback(SurfaceCallBack())


        val layoutParams = WindowManager.LayoutParams(
            1, 1,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                WindowManager.LayoutParams.TYPE_PHONE;
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(mSurfaceView, layoutParams)


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
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val message = "App is  recording a video"

        // promote service to foreground service
        ServiceCompat.startForeground(
            this,
            2,
            NotificationsHelper.buildNotification(this,message,intent),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
            } else {
                0
            }
        )
    }


}