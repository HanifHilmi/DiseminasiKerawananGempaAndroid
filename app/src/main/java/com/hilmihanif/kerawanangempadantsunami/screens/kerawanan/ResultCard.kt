package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hilmihanif.kerawanangempadantsunami.R

@Composable
fun ResultCard(
    modifier: Modifier = Modifier,
    viewModel: KerawananViewModel
){
    val resultCardUiState by viewModel.resultCardUiState.collectAsState()

    //viewModel.setLayerLoadingStatus()
    viewModel.updateLayerLoadStatus()

    ResultCardContent(isLayerLoaded = resultCardUiState.isLayerLoaded , modifier = modifier)
}



@Composable
fun ResultCardContent(

    modifier: Modifier = Modifier,
    isLayerLoaded:Boolean,
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
//            .padding(8.dp)
//            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLayerLoaded){
            Text(
                text = "Hasil",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(
                modifier = Modifier.padding(16.dp)
            ) {
                val imageModifier = Modifier.padding(horizontal = 20.dp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.result_gempatitle),
                        textAlign = TextAlign.Center,
                        //minLines= 3,
                        maxLines= 3,


                    )
                    Image(
                        painter = painterResource(id = R.drawable.img_earthquake),
                        contentDescription = "",
                        modifier = imageModifier,
                    )
                    Text(
                        text = stringResource(id = R.string.lorem10),
                        textAlign = TextAlign.Center
                    )//TODO
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.result_gmtitle),
                        textAlign = TextAlign.Center
                    )
                    Image(
                        painter = painterResource(id = R.drawable.img_groundmotion),
                        contentDescription = "",
                        modifier = imageModifier,
                    )
                    Text(text = stringResource(id = R.string.lorem10), textAlign = TextAlign.Center)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.result_tsutitle),
                        textAlign = TextAlign.Center
                    )
                    Image(
                        painter = painterResource(id = R.drawable.img_tsunami),
                        contentDescription = "",
                        modifier = imageModifier,
                    )
                    Text(text = stringResource(id = R.string.lorem10), textAlign = TextAlign.Center)
                }
            }
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Button(modifier = Modifier.weight(1f),
                    onClick = { /*TODO*/ }
                ) {
                    Text(text = "info Lengkap")
                }
                Button(modifier = Modifier.weight(1f),
                    onClick = { /*TODO*/ }
                ) {
                    Text(text = "Input Baru")
                }
            }

        }else{
            Text(text = "Loading layers...",modifier = modifier.padding(8.dp))
            Divider(modifier = Modifier.padding(6.dp))
            LinearProgressIndicator(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }


    }
}

@Preview
@Composable
fun PreviewResultCard() {
    ResultCardContent(
        isLayerLoaded = true
    )

}