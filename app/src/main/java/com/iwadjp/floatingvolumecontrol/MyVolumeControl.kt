package com.iwadjp.floatingvolumecontrol

import android.content.Context
import android.graphics.Point
import android.media.AudioManager
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class MyVolumeControl (context: Context) {

    companion object {
        private val TAG = MyVolumeControl::class.qualifiedName
    }

    private val am: AudioManager
    private val wm: WindowManager
    private val initVol: Int
    private var cVol: Int
    private val maxVol: Int
    private val minVol: Int
    private val pitch: Int

    init {
        am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        initVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        cVol = initVol
        maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        minVol = am.getStreamMinVolume(AudioManager.STREAM_MUSIC)

        val ds = wm.defaultDisplay
        val point: Point = Point()
        ds.getSize(point)
        pitch = point.x / (maxVol - minVol)
    }

    fun setVolume(vol: Int) {
        val nVol = vol % pitch
        Log.d(TAG, nVol.toString())
        if (nVol != cVol && nVol >= minVol && nVol <= maxVol) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, nVol, AudioManager.FLAG_SHOW_UI)
            cVol = nVol
        }
    }
}