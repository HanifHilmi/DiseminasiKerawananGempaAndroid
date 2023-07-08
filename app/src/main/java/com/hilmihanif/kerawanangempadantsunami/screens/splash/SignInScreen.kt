package com.hilmihanif.kerawanangempadantsunami.screens.splash

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.viewmodels.SignInState
import com.hilmihanif.kerawanangempadantsunami.viewmodels.SignInViewModel
import kotlinx.coroutines.launch

/*
@Composable
fun LoginContent(
    onClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.clickable { onClick() },
            text = "LOGIN",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.clickable { onSignUpClick() },
            text = "Sign Up",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Medium
        )
        Text(
            modifier = Modifier.clickable { onForgotClick() },
            text = "Forgot Password",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Medium
        )
    }


}
 */

@Composable
fun GoogleSignInScreen(
    googleAuthUiClient: GoogleAuthUiClient,
    onLoggedIn:(Boolean)-> Unit
) {
    val viewModel = viewModel<SignInViewModel>()
    val loginState = viewModel.signInState.collectAsState()
    val localLifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current

    // check if user is logged in
//    LaunchedEffect(key1 = Unit){
//        Log.d(AUTH_TEST,"current signed In ${googleAuthUiClient.getSignedInUser()}")
//        if(googleAuthUiClient.getSignedInUser() != null){
//            Log.d(AUTH_TEST,"current signed found In ${googleAuthUiClient.getSignedInUser()}")
//            onLoggedIn()
//        }
//    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {result->
            if(result.resultCode == Activity.RESULT_OK){
                localLifecycleOwner.lifecycleScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    viewModel.onSignInResult(signInResult)
                }
            }
        }
    )
    LaunchedEffect(key1 = loginState.value.isSignInSuccessful ){
        if (loginState.value.isSignInSuccessful){
            Toast.makeText(
                localContext,
                "Sign in berhasil",
                Toast.LENGTH_LONG
            ).show()
            onLoggedIn(true)

            viewModel.resetState()
        }

    }


    SignInScreen(
        onSignInClick = {
            localLifecycleOwner.lifecycleScope.launch {
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )

            }
        },
        state = loginState.value,
        onPassSignInClick = {
            onLoggedIn(false)
        }
    )
}

@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick:()-> Unit,
    onPassSignInClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = state.signInError){
        state.signInError?.let{error->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    SignInContent(onSignInClick = onSignInClick, onPassSignInClick = onPassSignInClick)

}

@Composable
fun SignInContent(
    onSignInClick: () -> Unit,
    onPassSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        Column(modifier = Modifier
            .align(Alignment.Center)
            .width(IntrinsicSize.Min)) {
            Image(
                painter = painterResource(id = R.drawable.img_placeholder),
                contentDescription = "",
            )
            Button(onClick = { onSignInClick() },modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    modifier = Modifier.height(24.dp)
                )
                Text(text = "Login dengan akun Google")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onPassSignInClick() },modifier = Modifier
                .height(IntrinsicSize.Min)
                .fillMaxWidth()) {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_google),
//                    contentDescription = "",
//                    modifier = Modifier.height(24.dp)
//                )
                Text(text = "Masuk tanpa login")
            }
        }

    }

}






@Preview
@Composable
fun PrevLoginScreen() {
//    LoginContent(onClick = { }, onSignUpClick = { }) {
//    }
    SignInContent(onSignInClick = {}, onPassSignInClick = {})
}