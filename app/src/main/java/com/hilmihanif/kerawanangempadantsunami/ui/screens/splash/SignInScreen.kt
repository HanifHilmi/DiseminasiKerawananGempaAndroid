package com.hilmihanif.kerawanangempadantsunami.ui.screens.splash

import android.app.Activity
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.viewmodels.SignInState
import com.hilmihanif.kerawanangempadantsunami.viewmodels.SignInViewModel
import kotlinx.coroutines.launch


@Composable
fun GoogleSignInScreen(
    googleAuthUiClient: GoogleAuthUiClient,
    onLoggedIn:(Boolean)-> Unit
) {
    val viewModel = viewModel<SignInViewModel>()
    val loginState = viewModel.signInState.collectAsState()
    val localLifecycleOwner = LocalLifecycleOwner.current
    val localContext = LocalContext.current
    var signInLoading by rememberSaveable { mutableStateOf(false)}

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

    SignInScreen(
        onSignInClick = {

            localLifecycleOwner.lifecycleScope.launch {
                signInLoading = true
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        },
        state = loginState.value,
        signInLoading = signInLoading,
        onSkipSignInClick = {
            onLoggedIn(false)
        }
    )
    LaunchedEffect(key1 = loginState.value.isSignInSuccessful){
        if (loginState.value.isSignInSuccessful){
            Toast.makeText(
                localContext,
                "Sign in berhasil",
                Toast.LENGTH_LONG
            ).show()
            onLoggedIn(true)

            //signInLoading = false
            viewModel.resetState()
        }else{
            if (loginState.value.signInError != null){
                Toast.makeText(
                    localContext,
                    "Sign In gagal ${loginState.value.signInError}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }



}



@Composable
fun SignInScreen(
    state: SignInState,
    signInLoading:Boolean,
    onSignInClick:()-> Unit,
    onSkipSignInClick: () -> Unit
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

    SignInContent(
        signInLoading = signInLoading,//!state.isSignInSuccessful,
        onSignInClick = onSignInClick,
        onSkipSignInClick = onSkipSignInClick
    )

}

@Composable
fun SignInContent(
    signInLoading:Boolean,
    onSignInClick: () -> Unit,
    onSkipSignInClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_loginscreen_art),
                contentDescription = "",
                modifier = Modifier.fillMaxHeight(.3f)
            )
            Text(text = "Silahkan lakukan Login", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                onClick = { onSignInClick() },
                enabled = !signInLoading

            ) {
                if(signInLoading){
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else{
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_google),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(text = "Login dengan akun Google")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onSkipSignInClick() },
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth()

            ) {

                Text(text = "Masuk tanpa login")
            }
        }

    }

}






@Preview(uiMode = UI_MODE_NIGHT_YES, name = "dark")
@Preview
@Composable
fun PrevLoginScreen() {
MaterialTheme {
    Surface {
        SignInContent(signInLoading  = true,onSignInClick = {}, onSkipSignInClick = {})
    }
}
}