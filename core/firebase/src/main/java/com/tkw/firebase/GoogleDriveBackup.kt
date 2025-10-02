package com.tkw.firebase

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.Collections
import javax.inject.Inject

class GoogleDriveBackup @Inject constructor(
    @ApplicationContext private val context: Context
): BackupManager, DriveAuthorize<AuthorizationResult> {
    private val requestedScopes = listOf(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))
    private val scope = listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)

    override fun upload(token: String?, file: java.io.File, backupFileName: String): String {
        val service = getDriveService(token)
        val mediaContent = FileContent("application/octet-stream", file)

        return try {
            val backupFile = getFile(token, backupFileName)
            val createdFile = if(backupFile == null) {
                // File's metadata.
                val fileMetadata = File()
                fileMetadata.setName(file.name)
                fileMetadata.setParents(Collections.singletonList("appDataFolder"))

                service.files().create(fileMetadata, mediaContent)
                    .setFields("id") // response에서 받을 필드 정의
                    .execute()
            } else {
                service.files().update(backupFile.id, null, mediaContent)
                    .setFields("id")
                    .execute()
            }

            println("File ID: " + createdFile.id)
            createdFile.id
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Unable to create file: " + e.details)
            throw handleGoogleDriveException(e)
        } catch (e: Exception) {
            System.err.println("Unexpected error during upload: ${e.message}")
            throw BackupError.UnknownError(e.message ?: "Unexpected error during upload")
        }
    }

    override fun download(token: String?, destFile: java.io.File, backupFileName: String) {
        val service = getDriveService(token)
        val outputStream = ByteArrayOutputStream()
        try {
            val backupFile = getFile(token, backupFileName)
            if(backupFile != null) {
                service.files().get(backupFile.id)
                    .executeMediaAndDownloadTo(outputStream)
                println("File ID: " + backupFile.id)
            }

            outputStream.writeTo(FileOutputStream(destFile))
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Unable to download file: " + e.details)
            throw handleGoogleDriveException(e)
        } catch (e: Exception) {
            System.err.println("Unexpected error during download: ${e.message}")
            throw BackupError.UnknownError(e.message ?: "Unexpected error during download")
        } finally {
            outputStream.close()
        }
    }

    private fun getFile(token: String?, fileName: String): File? {
        val list = getList(token)
        list.files.forEach {
            if(fileName == it.name) {
                return it
            }
        }
        return null
    }

    private fun getList(token: String?): FileList {
        val service = getDriveService(token)
        return try {
            val files = service.files().list()
                .setSpaces("appDataFolder")
                .setFields("files(id, name)")
                .execute()

            for(file in files.files) {
                System.out.printf("Found file: %s (%s)\n",
                    file.name, file.id)
            }
            files
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Unable to list files: " + e.details)
            throw handleGoogleDriveException(e)
        } catch (e: Exception) {
            System.err.println("Unexpected error during list: ${e.message}")
            throw BackupError.UnknownError(e.message ?: "Unexpected error during list")
        }
    }

    private fun getDriveService(token: String?): Drive {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
//        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        val credentials = GoogleCredentials.create(AccessToken(token, null))
            .createScoped(scope)
        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(
            credentials
        )
//        Build a new authorized API client service.
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer
        )
            .setApplicationName("OMaMul App Drive")
            .build()
    }

    override fun authorize(resultListener: (Result<AuthorizationResult>) -> Unit) {
        val authorizationRequest = AuthorizationRequest.builder().setRequestedScopes(requestedScopes).build()
        Identity.getAuthorizationClient(context)
            .authorize(authorizationRequest)
            .addOnSuccessListener {
                resultListener(Result.success(it))
            }
            .addOnFailureListener {
                it.printStackTrace()
                resultListener(Result.failure(it))
            }
    }

    private fun handleGoogleDriveException(e: GoogleJsonResponseException): BackupError {
        return when (e.statusCode) {
            403 -> {
                if (e.details?.message?.contains("quota", ignoreCase = true) == true ||
                    e.details?.message?.contains("storage", ignoreCase = true) == true) {
                    BackupError.QuotaExceeded(e.details.message ?: "Storage quota exceeded")
                } else {
                    BackupError.PermissionDenied(e.details?.message ?: "Permission denied")
                }
            }
            404 -> BackupError.FileNotFound(e.details?.message ?: "File not found")
            429 -> BackupError.RateLimitError(e.details?.message ?: "Too many requests")
            in 500..599 -> BackupError.ServerError(e.details?.message ?: "Server error")
            401 -> BackupError.AuthenticationError(e.details?.message ?: "Authentication failed")
            else -> BackupError.UnknownError(e.details?.message ?: "Unknown error: ${e.statusCode}")
        }
    }
}

sealed class BackupError(message: String) : Exception(message) {
    data class QuotaExceeded(val details: String) : BackupError("Google Drive quota exceeded: $details")
    data class AuthenticationError(val details: String) : BackupError("Authentication failed: $details")
    data class NetworkError(val details: String) : BackupError("Network error: $details")
    data class RateLimitError(val details: String) : BackupError("Rate limit exceeded: $details")
    data class FileNotFound(val details: String) : BackupError("File not found: $details")
    data class PermissionDenied(val details: String) : BackupError("Permission denied: $details")
    data class ServerError(val details: String) : BackupError("Server error: $details")
    data class UnknownError(val details: String) : BackupError("Unknown error: $details")
}