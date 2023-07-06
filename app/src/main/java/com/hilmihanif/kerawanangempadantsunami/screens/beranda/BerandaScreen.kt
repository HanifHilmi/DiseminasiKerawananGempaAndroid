package com.hilmihanif.kerawanangempadantsunami.screens.beranda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase_realtimedb.data.DataState
import com.hilmihanif.kerawanangempadantsunami.firebase_realtimedb.data.Gempa
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel
import com.hilmihanif.kerawanangempadantsunami.screens.main_map.MapControllerScreen
import com.hilmihanif.kerawanangempadantsunami.utils.BERANDA_SCREEN


@Composable
fun BerandaCardScreen(viewModel: MainMapViewModel) {




    //val result = viewModel.firebaseResponse.collectAsState()
    val latestresult = viewModel.latestResponse.collectAsState()
    //GempaHistoryContent(result = result.value)
    viewModel.setOnGempaPin(latestresult.value)

    MapControllerScreen(currentScreen = BERANDA_SCREEN, viewModel = viewModel) {
        BerandaCardContent(gempaData = latestresult.value, modifier = Modifier.fillMaxWidth())
    }


}

@Composable
fun BerandaCardContent(
    modifier:Modifier = Modifier,
    gempaData: Gempa
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(5.dp))
            .padding(8.dp)
            .width(IntrinsicSize.Max),
    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)) {
                Icon(imageVector = Icons.Default.Warning, contentDescription ="" )
                Text(text = " Gempabumi Terkini", style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_clock), contentDescription = "")
                    Text(text = "Waktu", style = MaterialTheme.typography.labelSmall)
                    Text(text = gempaData.Jam, style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id =R.drawable.ic_magnitude), contentDescription = "")
                    Text(text = "Magnitude", style = MaterialTheme.typography.labelSmall)
                    Text(text = gempaData.Magnitude, style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_depth), contentDescription = "")
                    Text(text = "Kedalaman", style = MaterialTheme.typography.labelSmall)
                    Text(text = gempaData.Kedalaman, style = MaterialTheme.typography.labelSmall)
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_pin_location), contentDescription = "")
                    Text(
                        text = gempaData.Wilayah,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 100.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "")
                    Text(text = "${gempaData.Lintang}-${gempaData.Bujur}", style = MaterialTheme.typography.labelSmall)
                }
            }
            if(gempaData.Terkirim != null){
                Text(text = "Terkirim pada ${gempaData.Terkirim}",
                        style = MaterialTheme.typography.labelSmall,modifier=Modifier.padding(6.dp) )
            }
        }
    }
}

@Composable
fun GempaHistoryContent(
    result  : DataState
) {

    when(result){
        is DataState.Failure->{}
        is DataState.Loading->{}
        is DataState.Success->{}
        else ->{}

    }

    Card(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .padding(16.dp)
        .fillMaxWidth()){


        Text(text = "")
    }
}
@Preview
@Composable
fun GempaHistoryPrev() {
    BerandaCardContent(
        gempaData =Gempa(
            Jam = "20:57:01 WIB",
            Magnitude = "3.7",
            Tanggal = "04 Jul 2023",
            Wilayah = "Pusat gempa berada di darat 14 km BaratDaya Bengkulu Tengah",
            Shakemap = "20230704205701.mmi.jpg",
            Lintang ="3.87 LS" ,
            Bujur = "102.38 BT",
            Coordinates = "-3.87,102.38",
            DateTime = "2023-07-04T13:57:01+00:00",
            Kedalaman = "6 km",
            Dirasakan = "II-III Kota Bengkulu, II-III Kepahiang",
            Potensi = "Gempa ini dirasakan untuk diteruskan pada masyarakat"
        )
    )
}








