package com.tkw.firebase

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tkw.domain.Authentication
import com.tkw.domain.model.GoogleInfo
import dagger.hilt.android.qualifiers.ActivityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GoogleOAuth @Inject constructor(
    @ActivityContext private val context: Context
): Authentication {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun fetchInfo(): GoogleInfo? {
        return auth.currentUser?.let {
            GoogleInfo(it.uid, it.displayName, it.photoUrl?.toString())
        }
    }

    override fun signIn(result: (Boolean) -> Unit) {
        val credentialManager = CredentialManager.create(context)

        //이전 ui로 구글 로그인 표시하는 경우
//        val googleIdOption = GetSignInWithGoogleOption
//            .Builder(context.getString(R.string.google_web_client_id))
//            .build()

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(true)
//            .setServerClientId(context.getString(R.string.google_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = context
                )
                handleSignIn(response, result)
            } catch (e: GetCredentialException) {
                e.printStackTrace()
            }
        }
    }

    override fun signOut() {
        auth.signOut()
        val credentialManager = CredentialManager.create(context)

        val request = ClearCredentialStateRequest()
        CoroutineScope(Dispatchers.Main).launch {
            credentialManager.clearCredentialState(request)
        }
    }

    override fun isLoggedIn(): Boolean =
        auth.currentUser != null

    private fun handleSignIn(response: GetCredentialResponse, result: (Boolean) -> Unit) {
        when(val credential = response.credential) {
            is CustomCredential -> {
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken, result)
                    } catch (e: GoogleIdTokenParsingException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("GoogleOAuth", "Unexpected type of credential")
                }
            }
            else -> {
                Log.e("GoogleOAuth", "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(token: String, result: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) {
                if(it.isSuccessful) {
                    val user = auth.currentUser
                    Log.d("signIn", "success : ${user?.displayName}")
                    result(true)
                } else {
                    Log.d("signIn", "failure")
                    result(false)
                }
            }
    }
}