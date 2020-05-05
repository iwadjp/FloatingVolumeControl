package com.iwadjp.floatingvolumecontrol

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.qualifiedName
        private val REQUEST_OVERLAY_PERMISSION = 1
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
