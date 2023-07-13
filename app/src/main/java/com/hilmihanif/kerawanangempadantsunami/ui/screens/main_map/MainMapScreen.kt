package com.hilmihanif.kerawanangempadantsunami.ui.screens.main_map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.ui.screens.beranda.BerandaScreen
import com.hilmihanif.kerawanangempadantsunami.ui.screens.kerawanan.KerawananScreen
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel
import com.hilmihanif.kerawanangempadantsunami.utils.BERANDA_SCREEN
import com.hilmihanif.kerawanangempadantsunami.utils.KERAWANAN_SCREEN


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainMapScreen(
    targetScreen :String,
    mainMapViewModel: MainMapViewModel = viewModel<MainMapViewModel>(),
) {

    val toggleList = stringArrayResource(id = R.array.toggle_list).toList()
    val multiplePermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
        )
    )

    val localcontext = LocalContext.current
    val mapUiState by mainMapViewModel.mapUiState.collectAsState()

    mainMapViewModel.updateMapDesc(mapUiState.map.loadStatus.collectAsState())
    mainMapViewModel.setInitToggleState(toggleList,multiplePermissions )
    mainMapViewModel.setInputDesc(localcontext)
    mainMapViewModel.updateLocatorLoading()

    MainMapContent(
        map = mainMapViewModel.mapUiState.collectAsState().value.map,
        viewpoint = mapUiState.currentViewPoint,
        mapStatus = mapUiState.map.loadStatus.collectAsState(),
        locatorTask = mapUiState.locatorTask!!,
        mapStatusDesc = mapUiState.currentMapStatusDesc,
        onSingleTap = { point, mapView ->
            //kerawananViewModel.setInitMapView(mapView)
            when (targetScreen) {
                BERANDA_SCREEN -> { }//TODO
                KERAWANAN_SCREEN -> {mainMapViewModel.setOnTapPinLocation(point, mapView, istapped = true) }
            }
        },
        onPinch = { mainMapViewModel.updateMapScale() },
        onInputToggleChange = {
            when (targetScreen) {
                BERANDA_SCREEN -> {}
                KERAWANAN_SCREEN -> {mainMapViewModel.updateToggleState(select = it, multiplePermissionsState  = multiplePermissions) }
            }
        },
        onProcessButtonClick = {
            when (targetScreen) {
                BERANDA_SCREEN -> {}
                KERAWANAN_SCREEN -> { mainMapViewModel.setOnProcessButtonClicked(
                        prov = mapUiState.currentPinLocation?.provinsi ?: ""
                    )
                }
            }
        },
        setInitMapView = {
            mainMapViewModel.setInitMapView(it,multiplePermissions)
        },
        isInputProcessNotDone = mapUiState.isInputProcessNotDone,
        currentScreen = targetScreen,
        viewModel = mainMapViewModel,

    )
}


/**
 * Set Kerawanan MapView to Composable
 */
@Composable
fun MainMapContent(
    map:ArcGISMap,
    viewpoint:Viewpoint,
    mapStatus: State<LoadStatus>,
    mapStatusDesc:String,
    isInputProcessNotDone:Boolean,
    locatorTask: LocatorTask,
    setInitMapView:(MapView)->Unit,
    onSingleTap: (Point?,MapView) -> Unit,
    onPinch: () -> Unit,
    onInputToggleChange:(String)->Unit,
    currentScreen :String,
    onProcessButtonClick:()->Unit,
    viewModel: MainMapViewModel
) = Box(
        modifier = Modifier
            .fillMaxSize(),
) {
    MapViewWithCompose(
        arcGISMap = map,
        viewpoint = viewpoint,
        onSingleTap = onSingleTap,
        onPinch = onPinch,
        setInitMapView = setInitMapView
    )
    Row(modifier = Modifier.align(Alignment.Center)) {
        Text(text = mapStatusDesc)
        AnimatedVisibility (mapStatus.value != LoadStatus.Loaded) {
            CircularProgressIndicator()
        }

    }

    AnimatedVisibility(
        visible = (mapStatus.value == LoadStatus.Loaded),
        modifier = Modifier
    ) {
        when(currentScreen){
            KERAWANAN_SCREEN->{
                KerawananScreen(
                    viewModel = viewModel,
                    isInputProcessNotDone = isInputProcessNotDone,
                    onInputToggleChange = onInputToggleChange,
                    locatorTask = locatorTask,
                    onProcessButtonClick = onProcessButtonClick
                )
            }
            BERANDA_SCREEN->{
                BerandaScreen(
                    viewModel = viewModel,
                )


            }
        }
    }
}




