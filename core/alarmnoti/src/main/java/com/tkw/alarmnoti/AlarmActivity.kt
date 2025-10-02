package com.tkw.alarmnoti

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class AlarmActivity: AppCompatActivity() {
    private var dismissButton: MaterialCardView? = null
    private var dragTrack: View? = null
    private var initialX = 0f
    private var initialTouchX = 0f
    private val dismissThreshold = 0.9f // 90% 이상 드래그 후 놓으면 종료

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
        setContentView(R.layout.activity_alarm_screen)

        // 현재 시간 설정
        val timeTextView = findViewById<TextView>(R.id.tv_alarm_time)
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        timeTextView.text = currentTime

        // 드래그 디스미스 설정
        dismissButton = findViewById(R.id.btn_dismiss)
        dragTrack = findViewById(R.id.drag_track)

        dismissButton?.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = view.x
                    initialTouchX = event.rawX
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = event.rawX - initialTouchX
                    val maxDragDistance = dragTrack?.width?.toFloat()?.minus(view.width) ?: 0f
                    val newX = (initialX + deltaX).coerceIn(initialX, initialX + maxDragDistance)
                    view.x = newX
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val deltaX = event.rawX - initialTouchX
                    val maxDragDistance = dragTrack?.width?.toFloat()?.minus(view.width) ?: 0f

                    // 우측 끝까지 드래그 후 놓으면 종료
                    if (deltaX >= maxDragDistance * dismissThreshold) {
                        finish()
                    } else {
                        // 임계값 미만이면 원위치로 되돌림
                        view.animate()
                            .x(initialX)
                            .setDuration(200)
                            .start()
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    // 취소 시 원위치로 되돌림
                    view.animate()
                        .x(initialX)
                        .setDuration(200)
                        .start()
                    true
                }
                else -> false
            }
        }

        // 자동 종료 타이머
        val timer = Timer()
        val timerTask = object: TimerTask() {
            override fun run() {
                finish()
            }
        }
        timer.schedule(timerTask, NotificationManager.TIMEOUT)
    }

    private fun turnScreenOnAndKeyguardOff(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED    // deprecated api 27
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD     // deprecated api 26
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON   // deprecated api 27
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
        val keyguardMgr = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardMgr.requestDismissKeyguard(this, null)
        }
    }

    private fun turnScreenOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        } else {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED    // deprecated api 27
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD     // deprecated api 26
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON   // deprecated api 27
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
    }
}