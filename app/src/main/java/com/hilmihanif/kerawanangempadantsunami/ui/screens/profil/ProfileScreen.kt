package com.hilmihanif.kerawanangempadantsunami.ui.screens.profil

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.GoogleAuthUiClient
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.UserData
import com.hilmihanif.kerawanangempadantsunami.viewmodels.SignInViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    authUiClient: GoogleAuthUiClient
) {
    val viewModel = viewModel<SignInViewModel>()
    val currentActivity  = LocalContext.current as Activity?
    val currentLifecycleOwner = LocalLifecycleOwner.current
    val loginState = viewModel.signInState.collectAsState()
    var isLoggedIn by rememberSaveable { mutableStateOf(authUiClient.isLoggedIn()) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {result->
            if(result.resultCode == Activity.RESULT_OK){
                currentLifecycleOwner.lifecycleScope.launch {
                    val signInResult = authUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )

                    viewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    ProfileContent(
        userData = authUiClient.getSignedInUser() ,
        isLoggedIn = isLoggedIn,
        onSignOut = {
            currentLifecycleOwner.lifecycleScope.launch {
                authUiClient.sighOut()
                Toast.makeText(
                    currentActivity,
                    "Signed Out",
                    Toast.LENGTH_LONG
                ).show()
                isLoggedIn = false
            }
        },
        onReSignIn = {
            currentLifecycleOwner.lifecycleScope.launch {
                val signInIntentSender = authUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        },
        aboutMeClicked = {}
    )



    LaunchedEffect(key1 = loginState.value.isSignInSuccessful ){
        if (loginState.value.isSignInSuccessful){
            Toast.makeText(
                currentActivity,
                "Sign in berhasil",
                Toast.LENGTH_LONG
            ).show()

            isLoggedIn = true
            //signInLoading = false
            viewModel.resetState()
        }else{
            if (loginState.value.signInError != null){
                Toast.makeText(
                    currentActivity,
                    "Sign In gagal ${loginState.value.signInError}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

//        if (loginState.value.signInError != null){
//            Toast.makeText(
//                currentActivity,
//                "Sign In gagal ${loginState.value.signInError}",
//                Toast.LENGTH_LONG
//            ).show()
//        }
    }




}

@Composable
fun ProfileContent(
    userData: UserData?,
    isLoggedIn:Boolean,
    onSignOut:() -> Unit,
    onReSignIn:() -> Unit,
    aboutMeClicked:() -> Unit,
) {


    Column {
        if (isLoggedIn){
            userData?.run {
                LoggedInProfilContent(
                    userData = userData,
                    onSignOut = onSignOut
                )
            } ?: CircularProgressIndicator()
        }else{
            NotLoggedInProfileContent(
                onReSignIn = onReSignIn
            )
        }
        SettingsContent()
        AboutContent(aboutMeClicked = aboutMeClicked)
    }

}

@Composable
fun NotLoggedInProfileContent(modifier:Modifier = Modifier, onReSignIn: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
    ){
        Text(
            text = "Anda Belum melakukan Sign In",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = onReSignIn) {

            Text(text = "Sign In")
        }
    }
}


@Composable
fun SettingsContent() {
    Column {

        Divider(thickness = 4.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Pengaturan",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,

        ) {
            Text(text = "Notifikasi Gempa",style = MaterialTheme.typography.bodyLarge,)
            Switch(
                checked = true,
                onCheckedChange = { },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Lokasi",style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = true,
                onCheckedChange = { },
            )
        }
    }
}


@Composable
fun AboutContent(
    aboutMeClicked:()->Unit={}
) {
    Column {

        Divider(thickness = 4.dp)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = "Tentang",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,

            ) {
            Icon(imageVector = Icons.Default.Info, contentDescription = "Info Icons")
            Text(text = "Versi Aplikasi ${BuildConfig.VERSION_NAME}",style = MaterialTheme.typography.bodyLarge,)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    aboutMeClicked()
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(imageVector = Icons.Default.AccountBox, contentDescription = "Info Icons")
            Text(text = "Tentang Saya", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun LoggedInProfilContent(
    modifier:Modifier = Modifier,
    userData:UserData,
    onSignOut: () -> Unit ={}
){

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
    ) {
        if (userData.profilePictureUrl != null){
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                placeholder = painterResource(id =R.drawable.ic_profile ),
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                //contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))

        }
        if (userData.username != null){
            Text(
                text = userData.username,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        if (userData.email != null){
            Text(
                text = userData.email,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        Button(onClick = onSignOut) {
            Text(text = "Sign Out")
        }
    }
}

@Preview
@Composable
fun PrevProfilScreen() {
    MaterialTheme{
        Surface {
            ProfileContent(
                userData = UserData(
                    userId = "fjaoefjoai",
                    username = "TEST USER",
                    email = "testemail_@gmail.com",
                    profilePictureUrl = ""
                ),
                isLoggedIn = true,
                onSignOut = {},
                onReSignIn = {},
                aboutMeClicked = {}
            )
        }
    }
}

