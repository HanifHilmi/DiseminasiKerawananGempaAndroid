package com.hilmihanif.kerawanangempadantsunami.firebase.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.utils.FIREBASE_TEST
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUiClient(
    private val context:Context,
    private val oneTapClient:SignInClient
) {
    private val auth = Firebase.auth

    suspend fun signIn():IntentSender?{
        Log.d(FIREBASE_TEST,"on signIn auth${auth}")
        val result = try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        }catch (e:Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        Log.d(FIREBASE_TEST,"on signIn resutl auth:${auth} $result")
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent):SignInResult{
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run{
                    UserData(
                        userId = uid,
                        username = displayName,
                        email = email,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e :Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun sighOut(){
        try{
            oneTapClient.signOut().await()
            auth.signOut()

        }catch (e:Exception){
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            email = email,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    fun isLoggedIn():Boolean = auth.currentUser != null

    private fun buildSignInRequest(): BeginSignInRequest{
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

}