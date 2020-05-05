package com.iwadjp.floatingvolumecontrol

import android.content.Context
import android.graphics.PixelFormat
import android.icu.text.Transliterator
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
    @RequiresApi(Build.VERSION_CODES.P)
    private lateinit var myVC: MyVolumeControl

    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT)
        .apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
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

    init {
        view.setOnTouchListener {
                view, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    initial = params.position - e.position
                    myVC = MyVolumeControl(context)
                }
                MotionEvent.ACTION_MOVE -> {
                    initial?.let {
                        params.position = it + e.position
                        windowManager.updateViewLayout(view, params)
                        myVC.setVolume(params.position.x)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    initial = null
                    // ToDo: myVC を nullにしたいけどできなし
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
}

