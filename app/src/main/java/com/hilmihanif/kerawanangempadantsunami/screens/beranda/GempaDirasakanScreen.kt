package com.hilmihanif.kerawanangempadantsunami.screens.beranda

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase_realtimedb.data.DataState
import com.hilmihanif.kerawanangempadantsunami.firebase_realtimedb.data.Gempa
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel


@Composable
fun GempaDirasakanList(result: DataState,viewModel:MainMapViewModel,mapControllerVisibile:(Boolean)->Unit ={}) {
    var isItemClicked by remember { mutableStateOf(false)}
    var gempaSelected by remember { mutableStateOf(Gempa())}

    if (!isItemClicked){
        when (result) {
            is DataState.Failure -> FailureBox(result.message)
            is DataState.Success -> GempaDirasakanBox(
                gempaList = result.data,
                onItemClicked = {
                    gempaSelected = it
                    isItemClicked = true
                }
            )

            else -> LoadingBox()
        }
        mapControllerVisibile(false)
    }else{
        mapControllerVisibile(true)
        GempaSelectedCard(
            title = "",
            gempaData = gempaSelected,
            isBackIcon = true,
            onBackIconClick = { isItemClicked = false },
            modifier = Modifier.fillMaxWidth(),
            setGempaPin = {
                viewModel.setOnGempaPin(it)
            }
        )
        BackHandler(isItemClicked) {
            isItemClicked = false
        }
    }



}

@Composable
fun GempaDirasakanBox(gempaList : MutableList<Gempa>,onItemClicked:(Gempa)->Unit) = Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
) {
    LazyColumn{
        items(gempaList.reversed()){gempa ->
            GempaCard(gempa = gempa,modifier = Modifier.padding(8.dp), onItemClicked = onItemClicked)

        }
    }
}


@Composable
fun GempaCard(gempa: Gempa,modifier: Modifier = Modifier,onItemClicked:(Gempa)->Unit) {
    Card(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(10))
            .clickable { onItemClicked(gempa) },
        elevation = CardDefaults.elevatedCardElevation(),

    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(color = Color.LightGray)
                        .padding(8.dp)
                        .width(IntrinsicSize.Min)
                ) {
                    //Text(text = "Magnitude")
                    Text(text = gempa.Magnitude)

                }
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = gempa.Wilayah,
                        maxLines = 3,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Row(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = ""
                        )
                        Column {
                            Text(text = gempa.Jam)
                            Text(text = gempa.Tanggal)
                        }
                    }
                    Row(modifier = Modifier.padding(8.dp)) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_pin_location),
                            contentDescription = ""
                        )
                        Text(text = gempa.Bujur)
                        Text(text = gempa.Lintang)
                    }
                }
            }

            if(gempa.Dirasakan.length > 3){
                Divider(thickness = 2.dp)
                Text(text = gempa.Dirasakan, style = MaterialTheme.typography.labelMedium,modifier = Modifier.padding(6.dp))
            }
        }
    }
}

@Composable
fun LoadingBox() = Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator()
}

@Composable
fun FailureBox(message: String) = Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.background),
    contentAlignment = Alignment.Center
) {
    Text(text = message, style = MaterialTheme.typography.displayMedium)
}


@Preview
@Composable
fun PrevCard() {
    GempaCard(Gempa(
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
    )){}
}