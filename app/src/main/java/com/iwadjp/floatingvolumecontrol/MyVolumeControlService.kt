package com.iwadjp.floatingvolumecontrol

import android.app.IntentService
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.RequiresApi
import kotlin.random.Random

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class MyVolumeControlService : IntentService("MyVolumeControlService") {    // Todo: IntentService は非推奨
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        val ACTION_START = "start"
        val ACTION_STOP = "stop"
    }

    private val notificationID = Random.nextInt()

    private var button: FloatingButton? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startNotification()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startNotification() {
        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
        val notification = Notification.Builder(this, MainActivity.CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(getString(R.string.notification_title))
            .build()
        startForeground(notificationID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == ACTION_START) {
            startOverlay()
        } else {
            stopSelf()
        }
        return Service.START_STICKY
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startOverlay() {
        Log.d(TAG, "startOverlay")
        ImageView(this).run {
            val windowManager = getSystemService(Service.WINDOW_SERVICE) as WindowManager
            setImageResource(android.R.drawable.ic_menu_add)
            button = FloatingButton(windowManager, this, context).apply {
                visible = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOverlay()
    }

    private fun stopOverlay() {
        button?.run {
            visible = false
        }
        button = null
    }

    override fun onHandleIntent(intent: Intent?) {}
}
