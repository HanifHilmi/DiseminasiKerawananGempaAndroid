package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import kotlinx.coroutines.launch


@Composable
fun MapViewWithCompose(
    arcGISMap: ArcGISMap,
    viewpoint: Viewpoint,
    onSingleTap: (Point?, MapView) -> Unit,
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


fun setBaseMap(): ArcGISMap {

    ArcGISEnvironment.apiKey = ApiKey.create(BuildConfig.API_KEY)

    val map = ArcGISMap(BasemapStyle.ArcGISImagery).apply {
        maxExtent = Envelope(91.404757,-8.65,109.586,7.956929, spatialReference = SpatialReference.wgs84())
        minScale = 10000000.0
        maxScale = 5000000.0
        initialViewpoint = Viewpoint(3.028, 98.905, 7000000.0)
    }

    return map
}


