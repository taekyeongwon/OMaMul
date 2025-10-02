package com.tkw.firebase

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.alarmnoti.NotificationManager
import com.tkw.domain.AlarmRepository
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.SettingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BackupForeground: Service() {

    @Inject
    lateinit var googleDrive: BackupManager

    @Inject
    lateinit var prefDataRepository: PrefDataRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationManager.notifyService(this, FOREGROUND_ID)
        startForeground(FOREGROUND_ID, notification)

        if(intent != null) {
            val isUpdate = intent.getBooleanExtra(EXTRA_IS_UPDATE, false)
            val accessToken = intent.getStringExtra(EXTRA_ACCESS_TOKEN)

            if (isUpdate) {
                doUpload(accessToken)
            } else {
                doBackUp(accessToken)
            }
        } else {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun doUpload(accessToken: String?) {
        val destRealmFile = File(applicationContext.filesDir, BACKUP_FILE_NAME)
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(ACTION_SERVICE_START))
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                googleDrive.upload(accessToken, destRealmFile, destRealmFile.name)
            }.onFailure { error ->
                error.printStackTrace()
                val errorIntent = Intent(ACTION_SERVICE_ERROR).apply {
                    when (error) {
                        is BackupError.QuotaExceeded -> {
                            putExtra(EXTRA_ERROR_TYPE, "quota_exceeded")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        is BackupError.AuthenticationError -> {
                            putExtra(EXTRA_ERROR_TYPE, "auth_error")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        is BackupError.NetworkError -> {
                            putExtra(EXTRA_ERROR_TYPE, "network_error")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        is BackupError.PermissionDenied -> {
                            putExtra(EXTRA_ERROR_TYPE, "permission_denied")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        else -> {
                            putExtra(EXTRA_ERROR_TYPE, "unknown_error")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message ?: "Unknown error occurred")
                        }
                    }
                }
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(errorIntent)
            }.onSuccess {
                prefDataRepository.saveLastSync(System.currentTimeMillis())
            }.also {
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(Intent(ACTION_SERVICE_STOP))
                stopSelf()
            }
        }
    }

    private fun doBackUp(accessToken: String?) {
        val sourceRealmFile = File(applicationContext.filesDir, DOWNLOAD_FILE_NAME)
        val destRealmFile = File(applicationContext.filesDir, BACKUP_FILE_NAME)
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(ACTION_SERVICE_START))
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                backUpRealm(accessToken, sourceRealmFile, destRealmFile)
            }.onFailure { error ->
                error.printStackTrace()
                val errorIntent = Intent(ACTION_SERVICE_ERROR).apply {
                    when (error) {
                        is BackupError.QuotaExceeded -> {
                            putExtra(EXTRA_ERROR_TYPE, "quota_exceeded")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        is BackupError.AuthenticationError -> {
                            putExtra(EXTRA_ERROR_TYPE, "auth_error")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        is BackupError.NetworkError -> {
                            putExtra(EXTRA_ERROR_TYPE, "network_error")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        is BackupError.PermissionDenied -> {
                            putExtra(EXTRA_ERROR_TYPE, "permission_denied")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message)
                        }
                        else -> {
                            putExtra(EXTRA_ERROR_TYPE, "unknown_error")
                            putExtra(EXTRA_ERROR_MESSAGE, error.message ?: "Unknown error occurred")
                        }
                    }
                }
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(errorIntent)
            }.onSuccess {
                prefDataRepository.saveLastSync(System.currentTimeMillis())
            }.also {
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(Intent(ACTION_SERVICE_STOP))
                wakeAllAlarm()
                stopSelf()
            }
        }
    }

    private suspend fun backUpRealm(accessToken: String?, sourceFile: File, destFile: File) {
        val backUpFileName = destFile.name
        googleDrive.download(accessToken, sourceFile, backUpFileName)
        settingRepository.merge(sourceFile, destFile)
        googleDrive.upload(accessToken, destFile, backUpFileName)
    }

    private suspend fun wakeAllAlarm() {
        if (
            NotificationManager.isNotificationEnabled(applicationContext)
            && prefDataRepository.fetchAlarmEnableFlag().first()
        ) {
            alarmRepository.wakeAllAlarm()
        }
    }

    companion object {
        const val FOREGROUND_ID = 1
        const val EXTRA_IS_UPDATE = "isUpdate"
        const val EXTRA_ACCESS_TOKEN = "accessToken"
        const val ACTION_SERVICE_START = "action_service_start"
        const val ACTION_SERVICE_STOP = "action_service_stop"
        const val ACTION_SERVICE_ERROR = "action_service_error"
        const val EXTRA_ERROR_TYPE = "error_type"
        const val EXTRA_ERROR_MESSAGE = "error_message"
        const val BACKUP_FILE_NAME = "default.realm"
        const val DOWNLOAD_FILE_NAME = "tmp.realm"
    }
}