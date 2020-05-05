package com.iwadjp.floatingvolumecontrol

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

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

        }
    }
}
