package com.hilmihanif.kerawanangempadantsunami.screens

import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import com.hilmihanif.kerawanangempadantsunami.mapTools.cekProvinsi
import com.hilmihanif.kerawanangempadantsunami.mapTools.reverseGeocoding
import com.hilmihanif.kerawanangempadantsunami.mapTools.setPin
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


val currentViewpoint = Viewpoint(3.028, 98.905, 7000000.0)
lateinit var locatorTask:LocatorTask

@Composable
fun KerawananScreen() {
    val composableScope = rememberCoroutineScope()
    val map by remember { mutableStateOf(setBaseMap(composableScope)) }

    val viewpoint by remember { mutableStateOf(currentViewpoint) }
    val mapStatus: State<LoadStatus> = map.loadStatus.collectAsState()
    var mapStatusDesc by remember { mutableStateOf("")}
    var reverseGeocoderesult by remember{ mutableStateOf(mapOf<String,Any?>())}



    mapStatusDesc = when (mapStatus.value) {
        LoadStatus.Loaded -> ""
        LoadStatus.Loading -> "Loading Map..."
        else -> "Failed to Load Map \n Check Connection"
    }

    KerawananContent(
        map = map,
        viewpoint = viewpoint,
        mapStatus = mapStatus,
        mapStatusDesc = mapStatusDesc,
        onSingleTap = {point, mapView ->
            composableScope.launch {
                reverseGeocoderesult = reverseGeocoding(point!!,mapView, locatorTask)
                Log.d(TEST_LOG,"revsersegeocode when tap $reverseGeocoderesult")
                if (cekProvinsi(reverseGeocoderesult).second){
                    setPin(
                        mapView = mapView,
                        mapPoint = point
                    ){
                        if (mapView.graphicsOverlays.isNotEmpty()) mapView.graphicsOverlays.removeLast()
                        mapView.graphicsOverlays.add(it)
                    }
                }

            }
        }
    )
}



/**
 * Set Kerawanan MapView to Composable
 */
@Composable
fun KerawananContent(map:ArcGISMap, viewpoint:Viewpoint, mapStatus: State<LoadStatus>,mapStatusDesc:String, onSingleTap: (Point?,MapView) -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        MapViewWithCompose(
            arcGISMap = map,
            viewpoint = viewpoint,
            onSingleTap = onSingleTap
        )
        Row(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(text = mapStatusDesc)
            if (mapStatus.value != LoadStatus.Loaded) {
                    CircularProgressIndicator()
            }

        }
    }
}


@Preview
@Composable
fun PreviewKerawananContent() {
    KerawananGempaDanTsunamiTheme {
        KerawananScreen()
    }
}


@Composable
fun MapViewWithCompose(
    arcGISMap: ArcGISMap,
    viewpoint: Viewpoint,
    onSingleTap: (Point?,MapView) -> Unit,
) {

    val lifecycleOwner = LocalLifecycleOwner.current


    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory ={context ->
            MapView(context).also { mapView ->
                lifecycleOwner.lifecycle.addObserver(mapView)
                mapView.map = arcGISMap
                lifecycleOwner.lifecycleScope.launch {
                    mapView.onSingleTapConfirmed.collect{
                        onSingleTap(it.mapPoint,mapView)
                    }
                }
            }

        },
        update = {view->
            view.map = arcGISMap
            lifecycleOwner.lifecycleScope.launch {
                view.setViewpointAnimated(viewpoint)

            }
        }
    )
}


fun setBaseMap(scope:CoroutineScope): ArcGISMap {

    ArcGISEnvironment.apiKey = ApiKey.create(BuildConfig.API_KEY)
    locatorTask = LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer").apply {
        this.apiKey = ApiKey.create(BuildConfig.API_KEY)
    }
    scope.launch {
        Log.d(TEST_LOG," load locator in setbasemap() before ${locatorTask.loadStatus.value}")
        locatorTask.load()
        Log.d(TEST_LOG," load locator in setbasemap() after ${locatorTask.loadStatus.value}")
    }



    val map = ArcGISMap(BasemapStyle.ArcGISImagery).apply {
        maxExtent = Envelope(91.404757,-8.65,109.586,7.956929, spatialReference = SpatialReference.wgs84())
        minScale = 10000000.0
        maxScale = 5000000.0
        initialViewpoint = Viewpoint(3.028, 98.905, 7000000.0)
    }



    return map
}
