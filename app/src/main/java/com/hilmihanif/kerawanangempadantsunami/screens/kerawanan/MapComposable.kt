package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
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
                mapView.map = arcGISMap
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
