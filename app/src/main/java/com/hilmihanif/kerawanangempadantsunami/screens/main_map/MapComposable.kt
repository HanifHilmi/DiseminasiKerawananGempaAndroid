package com.hilmihanif.kerawanangempadantsunami.screens.main_map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import kotlinx.coroutines.launch


@Composable
fun MapViewWithCompose(
    arcGISMap: ArcGISMap,
    viewpoint: Viewpoint,
    setInitMapView:(MapView)->Unit,
    onSingleTap: (Point?, MapView) -> Unit,
    onPinch: () -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current


    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory ={context ->
            MapView(context).also { mapView ->
                lifecycleOwner.lifecycle.addObserver(mapView)
                //Log.d(TEST_MAP_BUG,"mapview.map ${mapView.map}")
                mapView.map = arcGISMap
                ArcGISEnvironment.applicationContext = context
                //Log.d(TEST_MAP_BUG,"mapview.map ${mapView.map}")
                setInitMapView(mapView)
                lifecycleOwner.lifecycleScope.launch {
                    mapView.onSingleTapConfirmed.collect{
                        onSingleTap(it.mapPoint,mapView)
                    }
                }

                lifecycleOwner.lifecycleScope.launch {
                    mapView.onPan.collect{
                        onPinch()
                    }
                }
                lifecycleOwner.lifecycleScope.launch {
                    mapView.onDoubleTap.collect{
                        onPinch()
                    }
                }
                lifecycleOwner.lifecycleScope.launch {
                    mapView.onDoubleTap.collect{
                        onPinch()
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
