package com.hilmihanif.kerawanangempadantsunami.screens.beranda

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.hilmihanif.kerawanangempadantsunami.R

@Composable
fun ShakeMapDialog(url:String,onClose:()->Unit) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            ZoomableBox(modifier =Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = url,
                    contentDescription = "",
                    placeholder = painterResource(id = R.drawable.img_placeholder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offsetX
                            translationY = offsetY
                        }
                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onClose() }
            )
        }
    }
}

@Preview
@Composable
fun PrevShakemapComp() {
    ShakeMapDialog(url ="") {

    }

}