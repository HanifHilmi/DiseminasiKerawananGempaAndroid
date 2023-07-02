package com.hilmihanif.kerawanangempadantsunami.mapTools

import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.geometry.Envelope
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import com.hilmihanif.kerawanangempadantsunami.utils.MAP_MAX_SCALE
import com.hilmihanif.kerawanangempadantsunami.utils.MAP_MIN_SCALE

fun setBaseMap(): ArcGISMap {

    ArcGISEnvironment.apiKey = ApiKey.create(BuildConfig.API_KEY)

    val map = ArcGISMap(BasemapStyle.ArcGISImagery).apply {
        maxExtent = Envelope(91.404757,-8.65,109.586,7.956929, spatialReference = SpatialReference.wgs84())
        minScale = MAP_MIN_SCALE
        maxScale = MAP_MAX_SCALE
        initialViewpoint = Viewpoint(3.028, 98.905, (MAP_MAX_SCALE+ MAP_MIN_SCALE)/2)
    }

    return map
}