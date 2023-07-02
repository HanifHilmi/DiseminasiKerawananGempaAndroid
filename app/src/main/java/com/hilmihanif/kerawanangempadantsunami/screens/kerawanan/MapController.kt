package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arcgismaps.mapping.layers.Layer
import com.hilmihanif.kerawanangempadantsunami.utils.FAULT_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.GEMPA_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.GM_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.TSUNAMI_LAYER_INDEX


@Composable
fun MapControllerScreen(
    viewModel: KerawananViewModel,
    isLayerIdentified:Boolean
) {

    val state = viewModel.mapUiState.collectAsState()

    MapControllerContent(
        //operationalLayers = baseMap.operationalLayers,
        operationalLayers = state.value.map.operationalLayers,
        isLayerIdentified = isLayerIdentified
    )
}

@Composable
fun MapControllerContent(
    modifier: Modifier = Modifier,
    operationalLayers: MutableList<Layer>,
    isLayerIdentified:Boolean
) = Box(
        modifier = modifier.fillMaxSize(),
){
    var isLayersListExpanded by rememberSaveable { mutableStateOf(false)}
    var arrowAngle by rememberSaveable { mutableStateOf(0f)}
    val maxWidth = if(isLayersListExpanded) .35f else .3f


    Column(modifier =
    Modifier
        .padding(16.dp)
        .align(Alignment.TopEnd)
        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
        .padding(8.dp)
        .fillMaxWidth(maxWidth)
        .clickable {}
        .animateContentSize(),
    ){
        Row(modifier=Modifier.fillMaxWidth() ,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text(
                text = "Layers",
                style = MaterialTheme.typography.titleSmall,
                //modifier = Modifier.width(100.dp)
            )
            Image(
                modifier = Modifier
                    .clickable {
                        isLayersListExpanded = !isLayersListExpanded
                        arrowAngle = (arrowAngle + 180) % 360f
                    }
                    .rotate(arrowAngle),
                imageVector = Icons.Default.ArrowDropDown,

                contentDescription = "Expand layers"
            )
        }


        AnimatedVisibility(visible = isLayersListExpanded ) {
            Column{
                val mutablelist =  operationalLayers.map { it.isVisible }.toMutableStateList()
                var listCount by remember{ mutableStateOf(0)}

                operationalLayers.forEachIndexed{index, layer ->
                    listCount++
                    val text = when(index){
                        FAULT_LAYER_INDEX -> "Fault Model Layer"
                        GEMPA_LAYER_INDEX -> "Gempa Layer"
                        GM_LAYER_INDEX -> "Gerakan Tanah Layer"
                        TSUNAMI_LAYER_INDEX -> "Tsunami Layer"
                        else -> ""
                    }
                    Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Text(
                            text = text,
                            modifier = Modifier.weight(0.5f),
                            style =MaterialTheme.typography.labelSmall
                        )

                        Checkbox(
                            checked = mutablelist[index],
                            onCheckedChange = {
                                layer.isVisible = !layer.isVisible
                                mutablelist.add(index,layer.isVisible)
                            })// TODO( modify layer visibility when checked)
                    }
                    Divider()
                }

            }
            //isLayersListExpanded = false


        }
    }





}



@Preview(showBackground = false)
@Composable
fun PrevMapControl() {

    val mutableList = mutableListOf<Layer>()
    MapControllerContent(
        operationalLayers = mutableListOf<Layer>(), isLayerIdentified = false
    )

}