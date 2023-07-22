package com.hilmihanif.kerawanangempadantsunami.viewmodels

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.SignInResult
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInViewModel:ViewModel() {

    private val _state = MutableStateFlow(SignInState())

    val signInState = _state.asStateFlow()

    fun onSignInResult(result:SignInResult){
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
            )
        }
    }

    fun resetState(){
        _state.update { SignInState() }
    }


    fun setTopicSubscribed(context: Context, isSubscribed: Boolean) {
        viewModelScope.launch {
            PreferenceHelper.setTopicSubscribed(context, isSubscribed)
        }
    }

    fun observeTopicSubscribed(context: Context): Flow<Boolean> {
        return PreferenceHelper.observeTopicSubscribed(context)
    }


}

object PreferenceHelper {
    private const val PREFS_NAME = "my_prefs"
    private const val TOPIC = "notifikasi_gempa"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

    suspend fun subscribeToInitialTopicIfFirstTime(context: Context) {
        val isFirstTime = context.dataStore.data.first()[IS_FIRST_TIME] ?: true
        if (isFirstTime) {
            context.dataStore.edit { preferences ->
                preferences[IS_FIRST_TIME] = false
                // Subscribe to the initial topic here using FirebaseMessaging
                FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            }
        }
    }

    suspend fun setTopicSubscribed(context: Context, isSubscribed: Boolean) {
        if (isSubscribed){
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).await()
            Log.d(TEST_LOG,"FirebaseMessage subscibe${isSubscribed}")
        }else{
            FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC).await()
            Log.d(TEST_LOG,"FirebaseMessage subscibe${isSubscribed}")
        }
        context.dataStore.edit { preferences ->
            preferences[IS_TOPIC_SUBSCRIBED] = isSubscribed
        }
    }

    fun observeTopicSubscribed(context: Context): Flow<Boolean> {
        val dataStore = context.dataStore
        val currentValue = dataStore.data.map { preferences ->
            preferences[IS_TOPIC_SUBSCRIBED]  ?: true
        }
//            .stateIn(
//            scope = CoroutineScope(Dispatchers.IO),
//            started = SharingStarted.Lazily,
//            initialValue = true
//        )
        return currentValue
    }

    private val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
    private val IS_TOPIC_SUBSCRIBED = booleanPreferencesKey("is_topic_subscribed")
}

