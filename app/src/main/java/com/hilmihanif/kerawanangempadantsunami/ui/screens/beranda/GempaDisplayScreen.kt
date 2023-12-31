package com.hilmihanif.kerawanangempadantsunami.ui.screens.beranda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.DataState
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.Gempa


@Composable
fun GempaSelectedCard(
    title :String,
    modifier: Modifier = Modifier,
    gempaData: Gempa,
    isBackIcon:Boolean = false,
    onBackIconClick:()-> Unit ={},
    setGempaPin:(Gempa)->Unit={},
) {
    setGempaPin(gempaData)
    Card(
        modifier = modifier

            .padding(8.dp)
            //.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(5.dp))
//            .border()
            //.padding(8.dp)
            .width(IntrinsicSize.Max),
    ) {
        Column {
            Row(modifier = Modifier
                .padding(12.dp)
                .width(IntrinsicSize.Max),
            ) {
                if (!isBackIcon){
                    Icon(imageVector = Icons.Default.Warning, contentDescription ="" )
                    Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                }else{
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "",modifier.clickable { onBackIconClick() })
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_clock), contentDescription = "")
                    Text(text = "Waktu", style = MaterialTheme.typography.labelSmall)
                    Text(text = gempaData.Jam, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    Text(text = gempaData.Tanggal, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_magnitude), contentDescription = "")
                    Text(text = "Magnitude", style = MaterialTheme.typography.labelSmall)
                    Text(text = gempaData.Magnitude, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_depth), contentDescription = "")
                    Text(text = "Kedalaman", style = MaterialTheme.typography.labelSmall)
                    Text(text = gempaData.Kedalaman, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "",modifier = Modifier.height(30.dp))
                    Text(text = "Koordinat", style = MaterialTheme.typography.labelSmall)
                    Text(text = "${gempaData.Lintang}\n${gempaData.Bujur}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), verticalArrangement = Arrangement.SpaceEvenly) {

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(IntrinsicSize.Min)) {
                    Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_center), contentDescription = "")
                    Text(
                        text = gempaData.Wilayah,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    )
                }
                if((!gempaData.Dirasakan.isNullOrEmpty())&&(gempaData.Dirasakan.length) > 3){
                    Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.height(IntrinsicSize.Min)) {
                        Icon(imageVector = ImageVector.vectorResource(id = R.drawable.ic_sensor), contentDescription = "")
                        Text(
                            text = gempaData.Dirasakan,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "")
                    Text(
                        text = gempaData.Potensi,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        maxLines = 2
                    )
                }

            }
            if(gempaData.Terkirim != null){
                Text(text = "Terkirim pada ${gempaData.Terkirim}",
                    style = MaterialTheme.typography.labelSmall,modifier= Modifier.padding(6.dp) )
            }

            CustomUrlLinkText(
                modifier = modifier.padding(6.dp),
                style = TextStyle(textAlign = TextAlign.End),
                str = "Sumber data:",
                clickableLink = "data.bmkg.go.id",
                url = "https://data.bmkg.go.id"
            )

        }
    }
}
@Composable
fun CustomUrlLinkText(modifier :Modifier= Modifier,str:String,clickableLink:String,url:String,style:TextStyle = TextStyle()) {
    val anotatedString = buildAnnotatedString {
        //val str = "Sumber data:"
        //val clickable = "data.bmkg.go.id"
        val startIndex = str.length
        val endIndex = str.length + clickableLink.length

        append(str + clickableLink)
        //append(url)


        addStyle(style = MaterialTheme.typography.labelSmall.toSpanStyle(),0,endIndex)
        addStyle(style = SpanStyle(
            color = Color.Blue,
            textDecoration = TextDecoration.Underline
        ),startIndex,endIndex)

        addStringAnnotation(
            tag = "URL",
            //annotation = "https://data.bmkg.go.id",
            annotation = url,
            startIndex,
            endIndex
        )
        //append()
    }
    val mUriHandler = LocalUriHandler.current
    ClickableText(
        text = anotatedString,
        style =style,
        modifier= modifier.fillMaxWidth(),
        onClick = {
            anotatedString
                .getStringAnnotations("URL",it,it)
                .firstOrNull()?.let {  stringAnnotaton->
                    mUriHandler.openUri(stringAnnotaton.item)
                }

        }
    )
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
fun GempaTerkiniPrev() {
    MaterialTheme {
        Surface{
            GempaSelectedCard(
                modifier = Modifier.fillMaxWidth(),
                isBackIcon = true,
                title = "Gempa Terkini",
                gempaData = Gempa(
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
                    Potensi = "Gempa ini dirasakan untuk diteruskan pada masyarakat",
                    Terkirim = "test"
                )
            )
        }
    }
}