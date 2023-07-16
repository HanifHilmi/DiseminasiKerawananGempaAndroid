package com.hilmihanif.kerawanangempadantsunami.ui.screens.kerawanan

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hilmihanif.kerawanangempadantsunami.ui.screens.beranda.CustomUrlLinkText
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailInfoScreen(
    koordinat:String,
    krbGempa:Map<String,Any?>,
    krbGM:Map<String,Any?>,
    krbTsunami:Map<String,Any?>,
    visiblity:Boolean,
    onBackIconPressed:()->Unit


) {
    AnimatedVisibility(visible = visiblity) {
        BackHandler(visiblity) {
            onBackIconPressed()
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Informasi Lengkap")
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary),
                    navigationIcon = {
                        IconButton(onClick = { onBackIconPressed() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back button")
                        }
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(bottom = it.calculateBottomPadding())
                    .fillMaxSize()
                    .padding(16.dp), //contentAlignment = Alignment.TopStart
            verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Infomasi Kerawanan pada koordinat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = koordinat,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = "Kerawanan Gempa",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())
                    Text(
                        text = "Tingkat Kerawanan:",
                    )
                    Text(
                        text = if (krbGempa.containsKey("KELAS")) krbGempa["KELAS"].toString() else "tidak tersedia",
                    )
                    Text(
                        text = if (krbGempa.containsKey("NAMOBJ")) krbGempa["NAMOBJ"].toString() else "",
                    )
                    //Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = "Kerawanan Gerakan Tanah",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())
                    Text(
                        text = "Tingkat Kerawanan:",
                    )
                    Text(
                        text = if (krbGM.containsKey("REMARK")) krbGM["REMARK"].toString() else "tidak tersedia",
                    )
                    Text(
                        text = if (krbGM.containsKey("NAMOBJ")) krbGM["NAMOBJ"].toString() else "",
                    )
                    Text(
                        text = if (krbGM.containsKey("PROVINSI")) krbGM["PROVINSI"].toString() else "",
                    )

                    Spacer(modifier = Modifier.padding(8.dp))

                    Text(
                        text = "Kerawanan Tsunami",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Divider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())
                    Text(
                        text = "Tingkat Kerawanan:",
                    )
                    Text(
                        text = if (krbTsunami.containsKey("UNSUR")) krbTsunami["UNSUR"].toString() else "tidak tersedia",
                    )
                    Text(
                        text = if (krbTsunami.containsKey("KETERANGAN")) krbTsunami["KETERANGAN"].toString() else "",
                    )
                    Text(
                        text = if (krbTsunami.containsKey("WILAYAH")) krbTsunami["WILAYAH"].toString() else "",
                    )


                }
                CustomUrlLinkText(
                    modifier = Modifier,//.align(Alignment.BottomEnd),
                    str = "Sumber data KRB:",
                    clickableLink = "vsi.esdm.go.id/portalmbg",
                    url = "https://vsi.esdm.go.id/portalmbg/"
                )
            }

        }


    }


}

@Preview
@Composable
fun DetailInfoScreenPrev() {
    KerawananGempaDanTsunamiTheme {
        Surface {
            DetailInfoScreen(
                koordinat = "lat :0.0, long:0.0, Provinsi:0.0",
                mapOf(),
                mapOf(),
                mapOf(),
                visiblity = true,
                onBackIconPressed = {}
            )
        }
    }
}