package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arcgismaps.LoadStatus
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.R

//val toggleList = listOf<String>("Koordinat saat Ini","Tap melalui peta","Input Koordinat")

private lateinit var toggleList: List<String>

@Composable
fun InputKoordinatCard(
    modifier: Modifier = Modifier,
    inputDesc :String,
    locatorTask: LocatorTask? = null,
    errorMessage:Pair<Boolean,String>,
    toggleState:String,
    onProsesButtonClick:() -> Unit ={},
    onToggleChange :(String) -> Unit ={}
) {
    //var toggleState by remember {mutableStateOf(toggleList[0]) }
    //var errorMessage by remember {mutableStateOf(true to "Ini adalah Test Error")}

    toggleList = stringArrayResource(id = R.array.toggle_list).toList()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(5.dp))
            .padding(8.dp)


    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                painter = painterResource(R.drawable.img_lightbulbrad),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                alignment = Alignment.CenterStart ,

            )
            Text(
                text = stringResource(R.string.input_kerawanan_title),
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Gray,
                        offset = Offset(3.0f,-5.0f),
                        blurRadius =  2f
                    )
                )
            )
        }
        Divider(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
        )
        Column(modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 6.dp)
        ) {
            MultiToggleButton(
                currentSelection =  toggleState ,
                toggleStates = toggleList,
                onToggleChange = {
                    onToggleChange(it)
                }
            )
            Divider(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = 8.dp)
            )
            Text(
                text = inputDesc,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            )
            if(errorMessage.first){
                Row {
                    Image(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Red)
                    )
                    Text(text = errorMessage.second)
                }
            }
            val locatorStatus= locatorTask!!.loadStatus.collectAsState()
            if (locatorStatus.value != LoadStatus.Loaded){
                LinearProgressIndicator(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
            Button(
                modifier = Modifier
                    .align(Alignment.End),
                onClick = { onProsesButtonClick() }) {
                Text(text = "Proses")
            }

            when(toggleState){
                toggleList[0]->{

                }
                toggleList[1]->{

                }
                toggleList[2]->{

                }
            }
        }
    }
}


//@Preview(name="NightMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name="LightMode")
@Composable
fun PreviewCard() {
    InputKoordinatCard(
        errorMessage = true to "Ini adalah test pesan error",
        toggleState = stringArrayResource(id = R.array.toggle_list).toList()[1] ,
        inputDesc = "Tap pada peta untuk mendapatkan lokasi",
    )
}