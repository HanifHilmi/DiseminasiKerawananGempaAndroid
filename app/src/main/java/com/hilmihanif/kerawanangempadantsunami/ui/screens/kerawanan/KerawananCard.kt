package com.hilmihanif.kerawanangempadantsunami.ui.screens.kerawanan


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arcgismaps.LoadStatus
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.viewmodels.InputCardUiState
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel

private lateinit var toggleList: List<String>

@Composable
fun InputKoordinatCard(
    modifier: Modifier = Modifier,
    viewModel: MainMapViewModel,
    locatorTask: LocatorTask,
    onProsesButtonClick:() -> Unit ={},
    onToggleChange :(String) -> Unit ={}
){
    val context = LocalContext.current
    val kerawananUiState by viewModel.inputCardUiState.collectAsState()
    val locatorStatus by locatorTask.loadStatus.collectAsState()

    viewModel.setInputDesc(context)



    InputKoordinatCardContent(
        modifier = modifier,
        locatorStatus =locatorStatus,
        inputCardUiState = kerawananUiState,
        onProsesButtonClick = onProsesButtonClick,
        onToggleChange = onToggleChange,
        onLatFieldValueChanged = {
            viewModel.setLatLongFieldValue(lat= it)
        },
        onLongFieldValueChanged = {
            viewModel.setLatLongFieldValue(long= it)
        },
    )

}

@Composable
fun InputKoordinatCardContent(
    modifier: Modifier = Modifier,
    locatorStatus:LoadStatus,
    inputCardUiState: InputCardUiState,
    onProsesButtonClick:() -> Unit ={},
    onToggleChange :(String) -> Unit ={},
    onLatFieldValueChanged: (TextFieldValue) -> Unit ={},
    onLongFieldValueChanged: (TextFieldValue) -> Unit = {}
) {
    toggleList = stringArrayResource(id = R.array.toggle_list).toList()
    Card(
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(5.dp))
            .padding(8.dp),

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
                currentSelection =  inputCardUiState.toggleButtonState ,
                isLocationDisabled = inputCardUiState.isLocationDisabled,
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
                text = inputCardUiState.currentInputDesc,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            )

            if(inputCardUiState.currentErrorAlert.first){
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Image(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Red)
                    )
                    Text(
                        text = inputCardUiState.currentErrorAlert.second,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (locatorStatus != LoadStatus.Loaded){
                LinearProgressIndicator(
                    modifier= Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp)
                )
            }

//            var latFieldValue by remember {mutableStateOf(TextFieldValue("")) }
//            var longFieldValue by remember {mutableStateOf(TextFieldValue("")) }

            when(inputCardUiState.toggleButtonState){
                toggleList[0]->{}
                toggleList[1]->{}
                toggleList[2]->{
                    InputKoordinatTextField(
                        latlong = inputCardUiState.latTextFieldValue to inputCardUiState.longTextFieldValue ,
                        isInputError = false,
                        onLatFieldValueChanged = {
                            onLatFieldValueChanged(it)
//                            latFieldValue = it
                        },


                        onLongFieldValueChanged = {
                            onLongFieldValueChanged(it)
//                            longFieldValue = it
                        }
                    )
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.End),
                onClick = { onProsesButtonClick() },
                enabled = (inputCardUiState.pinnedLocation != null) && (inputCardUiState.pinnedLocation.provinsi != "Not Supported"  )
            ) {
                Text(text = "Proses")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputKoordinatTextField(

    latlong:Pair<TextFieldValue,TextFieldValue>,
    isInputError:Boolean,
    onLatFieldValueChanged:(TextFieldValue) -> Unit ={},
    onLongFieldValueChanged:(TextFieldValue) -> Unit = {},
) = Row(modifier = Modifier.fillMaxWidth()) {
    OutlinedTextField(
        value = latlong.first,
        onValueChange = {
            onLatFieldValueChanged(it)
        },
        singleLine = true,
        modifier = Modifier.weight(1f),
        //colors = TextFieldDefaults.textFieldColors(containerColor = dynamicLightColorScheme(LocalContext.current).surface),
        label = { Text(text="latitude") },
        isError = isInputError,
//        keyboardActions = ,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    OutlinedTextField(
        value = latlong.second,
        onValueChange = {
            onLongFieldValueChanged(it)
        },
        singleLine = true,
        modifier = Modifier.weight(1f),
        //colors = TextFieldDefaults.textFieldColors(containerColor = dynamicLightColorScheme(LocalContext.current).surface),
        label = { Text(text="longitude") },
        isError = isInputError,
//        keyboardActions = ,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )

}




//@Preview(name="NightMode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name="LightMode", showBackground = true,showSystemUi = true)
@Composable
fun PreviewCard() {
    val listtoggle = stringArrayResource(id = R.array.toggle_list)


    val previewState = InputCardUiState(
        toggleButtonState = listtoggle[1],
        currentErrorAlert = true to "TEST 123",
    )
    InputKoordinatCardContent(
        locatorStatus = LoadStatus.Loading,
        inputCardUiState = previewState,
        )

}


@Preview(showBackground = true)
@Composable
fun PreviewText() {
    InputKoordinatTextField(
        Pair(TextFieldValue("8090"),TextFieldValue("")),
        true
    )
}