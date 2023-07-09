package com.hilmihanif.kerawanangempadantsunami.screens.splash

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme
import com.hilmihanif.kerawanangempadantsunami.utils.AUTH_TEST
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SplashScreen(googleAuthUiClient: GoogleAuthUiClient, onSplashDone:(Boolean)-> Unit) {
    var startAnimation by remember {mutableStateOf(false)}
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000
        )
    )

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    LaunchedEffect(key1 = true){
        startAnimation = true
        Log.d(AUTH_TEST,"current signed In ${googleAuthUiClient.getSignedInUser()}")
        delay(3000)
        if(googleAuthUiClient.getSignedInUser() != null){
            Log.d(AUTH_TEST,"current signed found In ${googleAuthUiClient.getSignedInUser()}")
            //onLoggedIn()
            onSplashDone(true)

        }else{
            onSplashDone(false)
        }

    }
    if (!locationPermissionState.allPermissionsGranted){
        val allPermissionRevoked = locationPermissionState.permissions.size == locationPermissionState.revokedPermissions.size

        ToastShow(
            if (!allPermissionRevoked){
                "Permission had been granted"
            } else if(locationPermissionState.shouldShowRationale){
                "Permission is denied"
            }else{
                "Requesting location permission"
            }
        )
        LaunchedEffect(key1 = true){
            locationPermissionState.launchMultiplePermissionRequest()
        }

    }







    SplashScreenContent(alpha = alphaAnim.value)
}

@Composable
fun ToastShow(string: String) {
    val context = LocalContext.current
    LaunchedEffect(key1 = string){
        Toast.makeText(
            context,
            string,
            Toast.LENGTH_SHORT
        ).show()
    }
}


@Composable
fun SplashScreenContent(alpha:Float, modifier : Modifier = Modifier) {
    Box(modifier = modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            modifier = Modifier
                .padding(24.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .size(120.dp),
                imageVector = ImageVector.vectorResource(id = R.drawable.img_earthquake),
                contentDescription = "Splash Icon",
            )
            Text(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                text = "KERAWANAN GEMPA DAN TSUNAMI DAERAH SUMBAGUT",
                style = MaterialTheme.typography.titleLarge
            )
        }

    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun SplashPrev() {
    KerawananGempaDanTsunamiTheme {
        Surface{
            SplashScreenContent(1f)
        }
    }
}