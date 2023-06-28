package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.R


@Composable
fun KerawananScreen(
    kerawananViewModel: KerawananViewModel = viewModel()
) {
    val localcontext = LocalContext.current
    val kerawananUiState by kerawananViewModel.kerawananUiState.collectAsState()
    val toggleList = stringArrayResource(id = R.array.toggle_list).toList()
    //val composableScope = rememberCoroutineScope()
    val map by remember { mutableStateOf(setBaseMap()) }
    //var reverseGeocoderesult by remember{ mutableStateOf(mapOf<String,Any?>())}
    val mapStatus: State<LoadStatus> = map.loadStatus.collectAsState()
    //val locatorStatus: State<LoadStatus> = locatorTask.loadStatus.collectAsState()

    //var mapStatusDesc by remember { mutableStateOf("")}

//    val viewpoint by remember { mutableStateOf(currentViewpoint) }
//    var errorMessage by remember {mutableStateOf(false to "")}
//    var pinEnabled by remember { mutableStateOf(true)}
//    var toggleState by remember { mutableStateOf(toggleList[0])}
//    var tapEnabled by remember { mutableStateOf(true)}



    kerawananViewModel.updateMapDesc(mapStatus)
    kerawananViewModel.setInitToggleState(toggleList)
    kerawananViewModel.setInputDesc(localcontext)
    kerawananViewModel.updateLocatorLoading()



    KerawananContent(
        map = map,
        viewpoint = kerawananUiState.currentViewPoint,
        mapStatus = mapStatus,
        locatorTask = kerawananUiState.locatorTask!!,
        mapStatusDesc = kerawananUiState.currentMapStatusDesc,
        errorMessage = kerawananUiState.currentErrorAlert,
        toggleState = kerawananUiState.toggleButtonState,
        inputDesc = kerawananUiState.currentInputDesc,
        onSingleTap = {point, mapView ->
            kerawananViewModel.setOnTapPinLocation(point,mapView,toggleList)
        },
        onInputToggleChange= {
            kerawananViewModel.updateToggleState(select = it)
        }
    )
}


/**
 * Set Kerawanan MapView to Composable
 */
@Composable
fun KerawananContent(
    map:ArcGISMap,
    viewpoint:Viewpoint,
    mapStatus: State<LoadStatus>,
    mapStatusDesc:String,
    inputDesc:String,
    locatorTask: LocatorTask,
    toggleState: String,
    errorMessage:Pair<Boolean,String>,
    onSingleTap: (Point?,MapView) -> Unit,
    onInputToggleChange:(String)->Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        MapViewWithCompose(
            arcGISMap = map,
            viewpoint = viewpoint,
            onSingleTap = onSingleTap,
        )
        Row(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = mapStatusDesc)
            if (mapStatus.value != LoadStatus.Loaded) {
                CircularProgressIndicator()
            }

        }
        if(mapStatus.value == LoadStatus.Loaded){
            InputKoordinatCard(
                modifier = Modifier.align(Alignment.BottomCenter),
                errorMessage = errorMessage,
                toggleState = toggleState,
                inputDesc = inputDesc,
                onToggleChange = { onInputToggleChange(it) },
                locatorTask = locatorTask,
                onProsesButtonClick = {}
            )
        }
    }
}

