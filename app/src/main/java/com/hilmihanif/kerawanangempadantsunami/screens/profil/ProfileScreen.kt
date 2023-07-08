package com.hilmihanif.kerawanangempadantsunami.screens.profil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.auth.UserData

@Composable
fun ProfileScreen(
    userData: UserData?,
    isLoggedIn:Boolean,
    onSignOut:() -> Unit,
    onReSignIn:() -> Unit,
) {

    if (isLoggedIn){
        userData?.run {
            LoggedInProfilScreen(
                userData = userData,
                onSignOut = onSignOut
            )
        } ?: CircularProgressIndicator()
    }else{
        NotLoggedInProfileScreen(
            onReSignIn = onReSignIn
        )
    }

}

@Composable
fun NotLoggedInProfileScreen(modifier:Modifier = Modifier, onReSignIn: () -> Unit) {
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
fun LoggedInProfilScreen(
    modifier:Modifier = Modifier,
    userData:UserData,
    onSignOut: () -> Unit ={}
){

    Column(
        modifier = modifier
            .fillMaxSize()
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
    ProfileScreen(
        userData = UserData(
            userId = "fjaoefjoai",
            username = "TEST USER",
            email = "testemail@gmail.com",
            profilePictureUrl = ""
        ),
        isLoggedIn = false,
        onSignOut = {},
        onReSignIn = {}
    )
}

