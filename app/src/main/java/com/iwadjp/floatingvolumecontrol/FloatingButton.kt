package com.iwadjp.floatingvolumecontrol

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class FloatingButton(val windowManager: WindowManager, val view: View, context: Context) {
    companion object {
        private val TAG = FloatingButton::class.qualifiedName
    }
    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT)
        .apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100     // ToDo: 初期位置をボリュームと合わせる？　使い勝手が悪くなるかも
            y = 100
        }

    var visible: Boolean = false
        set(value) {
            if (field != value) {
                field = value
            }
            if (value) {
                windowManager.addView(view, params)
            } else {
                windowManager.removeView(view)
            }
        }

    private var initial: Position? = null
    private var myVC: MyVolumeControl? = null

    init {
        view.setOnTouchListener {
                view, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(TAG, "params=${params.position}, e=${e.position}")
                    initial = params.position - e.position
                    Log.d(TAG, "initial=$initial")
                    myVC = MyVolumeControl(context)
                }
                MotionEvent.ACTION_MOVE -> {
                    initial?.let {
                        Log.d(TAG, "it=${it}, e=${e.position}")
                        params.position = it + e.position
                        Log.d(TAG, "params=${params.position}")
                        windowManager.updateViewLayout(view, params)
                        myVC?.setVolume(params.position.x)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    initial = null
                    myVC = null
                }
            }
            false
        }
        view.setOnClickListener {
            Log.d(TAG, "onClick")
        }
    }

    private val MotionEvent.position: Position
        get() = Position(rawX, rawY)

    private var WindowManager.LayoutParams.position: Position
        get() = Position(x.toFloat(), y.toFloat())
        set(value) {
            x = value.x
            y = value.y
        }

    private data class Position(val fx: Float, val fy: Float) {
        val x: Int
            get() = fx.toInt()

        val y: Int
            get() = fy.toInt()

        operator fun plus(p: Position) = Position(fx + p.fx, fy + p.fy)
        operator fun minus(p: Position) = Position(fx - p.fx, fy - p.fy)
    }

    fun setPosition(context: Context) {
        myVC = MyVolumeControl(context)
        myVC?.let {
            val fx = myVC!!.getPosition()
            val position = Position(fx, 100.toFloat())    // ToDo: ハードコードNG
           params.position = position
        }
    }
}

