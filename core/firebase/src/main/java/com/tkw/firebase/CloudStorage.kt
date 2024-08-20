package com.tkw.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.tkw.domain.BackupManager
import java.io.File


class CloudStorage: BackupManager {
    private val storage: FirebaseStorage = Firebase.storage
    private val storageRef = storage.reference.child("users")

    override fun upload(uid: String?, file: File): String {    //firebase uid값 전달
        val fileUri = Uri.fromFile(file)
        val fileRef = storageRef.child(uid!!).child(fileUri.lastPathSegment ?: "")
        val uploadTask = fileRef.putFile(fileUri)
        uploadTask.addOnSuccessListener {
            Log.d("test", "upload success")
        }.addOnFailureListener {
            Log.d("test", "upload failure")
        }
        return ""
    }

    override fun download() {
        TODO("Not yet implemented")
    }
}