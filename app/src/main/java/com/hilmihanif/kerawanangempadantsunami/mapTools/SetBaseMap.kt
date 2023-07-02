package com.hilmihanif.kerawanangempadantsunami.mapTools

import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.hilmihanif.kerawanangempadantsunami.BuildConfig

fun setBaseMap(): ArcGISMap {

    ArcGISEnvironment.apiKey = ApiKey.create(BuildConfig.API_KEY)

    val map = ArcGISMap(BasemapStyle.ArcGISImagery).apply {
        maxExtent = Envelope(91.404757,-8.65,109.586,7.956929, spatialReference = SpatialReference.wgs84())
        minScale = 10e6
        maxScale = 1e6
        initialViewpoint = Viewpoint(3.028, 98.905, 7e6)
    }

    return map
}