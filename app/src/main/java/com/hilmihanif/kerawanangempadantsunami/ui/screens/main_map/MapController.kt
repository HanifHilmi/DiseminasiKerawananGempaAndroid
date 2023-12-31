package com.hilmihanif.kerawanangempadantsunami.ui.screens.main_map

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.arcgismaps.mapping.layers.Layer
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.Gempa
import com.hilmihanif.kerawanangempadantsunami.ui.screens.beranda.GempaSelectedCard
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme
import com.hilmihanif.kerawanangempadantsunami.utils.*



@Composable
fun MapControllerScreen(
    modifier: Modifier = Modifier,
    currentScreen:String,
    visibility:Boolean = true,
    viewModel: MainMapViewModel,
    onShakemapClick:() -> Unit={},
    content:@Composable () -> Unit
) {

    val state by viewModel.mapUiState.collectAsState()
    val mapScale by viewModel.mapScale.collectAsState()

    viewModel.updateMapScale()

    MapControllerContent(
        //operationalLayers = baseMap.operationalLayers,
        operationalLayers = state.map.operationalLayers,
        zoomScale =(mapScale/ MAP_MAX_SCALE).toFloat() ,
        onZoomSliderChanged = {
            viewModel.setMapScale(it)
        },
        visibility = visibility,
        currentScreen = currentScreen,
        modifier = modifier,
        content = content,
        onShakemapClick = onShakemapClick

    )

}


@Composable
fun MapControllerContent(
    modifier: Modifier = Modifier,
    operationalLayers: MutableList<Layer>,
    zoomScale:Float,
    visibility:Boolean = true,
    currentScreen: String,
    onShakemapClick:()->Unit ={},
    onZoomSliderChanged:(Float)->Unit = {},
    content:@Composable ()->Unit
){
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .weight(if (visibility) 1f else .1f),
        ) {
            var isLayersListExpanded by rememberSaveable { mutableStateOf(false) }
            var isZoomSliderExpanded by rememberSaveable { mutableStateOf(false) }
            var arrowAngle by rememberSaveable { mutableFloatStateOf(0f) }
            val maxWidth = if (isLayersListExpanded) .35f else .3f


            if(visibility){
                Column(
                    modifier = modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                        .fillMaxWidth(maxWidth)
                        .clickable {
                            isLayersListExpanded = !isLayersListExpanded
                            arrowAngle = (arrowAngle + 180) % 360f
                        }
                        .animateContentSize(),
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Layers",
                            style = MaterialTheme.typography.labelMedium,
                            //modifier = Modifier.width(100.dp)
                        )
                        Icon(
                            modifier = Modifier
                                .rotate(arrowAngle),
                            imageVector = Icons.Default.ArrowDropDown,
                            //tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Expand layers"
                        )
                    }
                    AnimatedVisibility(visible = isLayersListExpanded) {
                        Column {
                            val mutablelist = operationalLayers.map { it.isVisible }.toMutableStateList()
                            var listCount by remember { mutableIntStateOf(0) }

                            operationalLayers.forEachIndexed { index, layer ->
                                listCount++
                                val text = when (index) {
                                    FAULT_LAYER_INDEX -> "Fault Model Layer"
                                    GEMPA_LAYER_INDEX -> "Gempa Layer"
                                    GM_LAYER_INDEX -> "Gerakan Tanah Layer"
                                    TSUNAMI_LAYER_INDEX -> "Tsunami Layer"
                                    else -> ""
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = text,
                                        modifier = Modifier.weight(0.5f),
                                        style = MaterialTheme.typography.labelSmall
                                    )

                                    Checkbox(
                                        checked = mutablelist[index],
                                        onCheckedChange = {
                                            layer.isVisible = !layer.isVisible
                                            mutablelist.add(index, layer.isVisible)
                                        })// TODO( modify layer visibility when checked)
                                }
                                Divider()
                            }

                        }
                    }
                }

                Column(
                    modifier = modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(
                            MaterialTheme.colorScheme.background,
                            RoundedCornerShape(CornerSize(50))
                        )
                        .padding(8.dp)
                        .clickable {}
                        .animateContentSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(visible = isZoomSliderExpanded) {
                        Column {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.zoom_out_24px),
                                contentDescription = "",
                                modifier = Modifier.clickable { isZoomSliderExpanded = !isZoomSliderExpanded },
                                tint = MaterialTheme.colorScheme.surfaceTint
                            )
                            VerticalSlider(
                                sliderValue = zoomScale,
                                onZoomSliderChanged = onZoomSliderChanged
                            )
                        }

                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.zoom_in_24px),
                        contentDescription = "",
                        modifier = Modifier.clickable { isZoomSliderExpanded = !isZoomSliderExpanded },
                        //tint = MaterialTheme.colorScheme.surfaceTint
                    )


                }
                when (currentScreen) {
                    KERAWANAN_SCREEN -> {
                        var isLegendDialogShowed by rememberSaveable { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.BottomEnd)
                                .background(
                                    MaterialTheme.colorScheme.background,
                                    RoundedCornerShape(CornerSize(50))
                                )
                                .padding(8.dp)
                                .clickable {}
                                .animateContentSize(),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "",
                                modifier = Modifier.clickable {
                                    isLegendDialogShowed = true
                                }
                            )
                            if (isLegendDialogShowed) {
                                MapLegendDialog(
                                    onClose = {
                                        isLegendDialogShowed = false
                                    }
                                )
                            }
                        }
                    }

                    BERANDA_TAB_0 -> {
                        ShakeMapButton{
                            onShakemapClick()
                        }
                    }
                    BERANDA_TAB_1 ->{
                        ShakeMapButton{
                            onShakemapClick()
                        }
                    }

                }
            }
        }

        content()
    }
}

@Composable
fun BoxScope.ShakeMapButton(onShakemapClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd)
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
            .clickable { onShakemapClick() }
            .animateContentSize(),
    ) {
        Text(text = "Shakemap", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun VerticalSlider(
    modifier :Modifier = Modifier,
    sliderValue :Float,
    onZoomSliderChanged:(Float) -> Unit
) {
    Slider(
        value = sliderValue,
        onValueChange ={
            onZoomSliderChanged(it)
        },
        valueRange = 0f..10f,
        modifier = modifier
            .graphicsLayer {
                rotationZ = 270f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
            .width(120.dp)
            .height(30.dp)
    )

}



@Preview(name = "Dark Mode",uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PrevMapControl() {
    KerawananGempaDanTsunamiTheme {
        Surface {
            MapControllerContent(
                operationalLayers = mutableListOf(),
                zoomScale = 7f,
                currentScreen = BERANDA_TAB_0
            ){
                GempaSelectedCard(
                    title="Gempa Terkini",
                    gempaData = Gempa(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}