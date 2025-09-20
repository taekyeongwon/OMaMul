package com.tkw.omamul

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import com.tkw.alarmnoti.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

@HiltAndroidApp
class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        setUncaughtExceptionHandler()
        initNotification()
    }

    private fun setUncaughtExceptionHandler() {
        val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.d(thread.name, getStackTrace(throwable))

            defaultHandler?.uncaughtException(thread, throwable)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    private fun getStackTrace(e: Throwable?): String {
        val result = StringWriter()
        val printWriter = PrintWriter(result)

        var th: Throwable? = e
        while(th != null) {
            th.printStackTrace(printWriter)
            th = th.cause
        }

        val stackTraceAsString = result.toString()
        printWriter.close()

        return stackTraceAsString
    }

    private fun initNotification() {
        // Compose Navigation으로 마이그레이션되어 Deep Link 설정 제거
        // 알림 클릭 시 메인 액티비티로 이동하도록 변경
        NotificationManager.createNotificationChannel(this)
        // NotificationManager.setContentClickPendingIntent(pendingIntent)
    }

    companion object {
        var sharedPref: SharedPreferences? = null
    }
}