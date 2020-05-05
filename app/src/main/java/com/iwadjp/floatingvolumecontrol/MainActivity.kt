package com.iwadjp.floatingvolumecontrol

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private val REQUEST_OVERLAY_PERMISSION = 1
        val CHANNEL_ID = "FloatingVolumeControl"
    }

    private var enable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        if (hasOverlayPermission()) {
            val intent = Intent(this, MyVolumeControlService::class.java)
                .setAction(MyVolumeControlService.ACTION_STOP)  // TODO: STOP と STARTは合っている？
            startService(intent)
        } else {
            requestOverlayPermission(REQUEST_OVERLAY_PERMISSION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // ToDo: 二重登録をやめる必要がある？
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onStop() {
        super.onStop()
        if (enable && hasOverlayPermission()) {
            val intent = Intent(this, MyVolumeControlService::class.java)
                .setAction(MyVolumeControlService.ACTION_START)
            startService(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_OVERLAY_PERMISSION -> Log.d(TAG, "enable overlay permisson")
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        enable = false
        return super.onTouchEvent(event)
    }

}
