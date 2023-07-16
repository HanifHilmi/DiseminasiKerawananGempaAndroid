package com.hilmihanif.kerawanangempadantsunami.ui.screens.kerawanan

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hilmihanif.kerawanangempadantsunami.R

@Composable
fun PenjelasanAlertDialog(
    title:String,
    icon: ImageVector,
    onDismiss:()->Unit,
) {
    var titleList:List<String>? = null
    var bodyList:List<String>? = null
    when{
        title.contains("gempa",true)->{
            titleList = stringArrayResource(id = R.array.title_penjelasan_gempa).toList()
            bodyList = stringArrayResource(id = R.array.penjelasan_gempa).toList()
        }
        title.contains("tanah",true)->{
            titleList = stringArrayResource(id = R.array.title_penjelasan_gm).toList()
            bodyList = stringArrayResource(id = R.array.penjelasan_gm).toList()
        }
        title.contains("tsunami",true)->{
            titleList = stringArrayResource(id = R.array.title_penjelasan_tsunami).toList()
            bodyList = stringArrayResource(id = R.array.penjelasan_tsunami).toList()
        }
    }

    var listToggle by rememberSaveable { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = {onDismiss()},
    ){
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                .width(IntrinsicSize.Max)
                .animateContentSize(),
            horizontalAlignment = Alignment.End
        ){
            Row(modifier = Modifier.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = "")
                Text(text = title,modifier = Modifier.fillMaxWidth())
            }
            Column(modifier = Modifier.padding(12.dp)) {
                titleList?.forEachIndexed { index, text ->
                    Divider(thickness = 2.dp)
                    Column(
                        modifier = Modifier
                            .clickable { listToggle = index }
                            .fillMaxWidth()
                            .animateContentSize()
                    ){

                        Text(text = text,style = MaterialTheme.typography.titleSmall)
                        if(index == listToggle){
                            Text(
                                text = bodyList?.get(index) ?: "",
                                style=MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            TextButton(onClick = { onDismiss() }) {
                Text(text = "TUTUP")
            }
        }



    }


}

@Preview
@Composable
fun PreviewDialog() {
    PenjelasanAlertDialog(title = "GEMPA", icon = Icons.Default.Create,{})
}