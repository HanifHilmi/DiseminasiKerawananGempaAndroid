package com.hilmihanif.kerawanangempadantsunami.mapTools


import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.graphics.drawable.toDrawable
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.symbology.PictureMarkerSymbol
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.GeocodeResult
import com.arcgismaps.tasks.geocode.LocatorTask
import com.arcgismaps.tasks.geocode.ReverseGeocodeParameters
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import kotlinx.coroutines.delay


private lateinit var graphicsOverlays:MutableList<GraphicsOverlay>
suspend fun reverseGeocoding(
    mapPoint: Point,
    mMapView:MapView,
    locatorTask:LocatorTask
): Map<String, Any?>
{
    var geocodeList: List<GeocodeResult>? = null
//                locatorTask.reverseGeocode(mapPoint!!, ReverseGeocodeParameters().apply {
//                outputSpatialReference = mMapView.spatialReference.value
//            }).getOrNull()
//            Log.d(TEST_LOG,"locator task ${this}, ${locatorTask.loadStatus.value}")
//            Log.d(TEST_LOG,"geocode atrributes ${geocodeList}")
//
    do {
        Log.d(TEST_LOG, "locator task ${mMapView.context}, ${locatorTask.loadStatus.value}")
        if (locatorTask.loadStatus.value != LoadStatus.Loaded) {
            if (locatorTask.loadStatus.value != LoadStatus.Loading) {
                locatorTask.retryLoad()
                Log.d(TEST_LOG, "retrying LocatorTask")
                delay(100)
                continue
            }
        }
        geocodeList = locatorTask.reverseGeocode(mapPoint, ReverseGeocodeParameters().apply {
            outputSpatialReference = mMapView.spatialReference.value
        }).getOrNull()

        Log.d(TEST_LOG, "geocode atrributes ${geocodeList}")

    } while (geocodeList.isNullOrEmpty())


    val geocode = geocodeList[0]
    //            Log.d(TEST_LOG, "geocode atrributes : ${geocode.attributes.toString()}")
    if(cekProvinsi(geocode.attributes).second){
        geocode.displayLocation!!.let {
            Log.d(TEST_LOG, "display Location ${it.y}")

//            mMapView.setViewpointCenter(
//                Point(
//                    it.x,
//                    it.y - 2 * Math.pow(10.0, 5.0),
//                    it.spatialReference
//                )
//            )
        }
    }
    return geocode.attributes
}


fun cekProvinsi(map:Map<String,Any?>):Pair<String,Boolean>{

    if (map.isNotEmpty()){
        map.let {
            if (map.getValue("CountryCode") == "IDN") {
                if (map.getValue("Region") == "Aceh") return "Aceh" to true
                else if (map.getValue("Region") == "Sumatera Utara") return "Sumatera Utara" to true
                else if (map.getValue("Region") == "Sumatera Barat")  return "Sumatera Barat" to true
                else if (map.getValue("Region") == "Riau")  return "Riau" to true
                else  return "Not Supported" to true
            } else return "Not Available" to false
        }
    } else return "Failed to Load" to false


}
 fun setPin(
    mapView:MapView,
    mapPoint: Point?,
    setGraphicsOverlay: (GraphicsOverlay) -> Unit
): Point? {
    //val mGraphicsOver = mapView.graphicsOverlays
     val context = mapView.context

    val mGraphicsOverlay = GraphicsOverlay()
    mGraphicsOverlay.graphics.let { graphicList ->
        val wgs84Point =
            GeometryEngine.projectOrNull(mapPoint!!, SpatialReference.wgs84())


        val pinImageBitmap = BitmapFactory.decodeResource(context.resources,R.drawable.placeholder)
            .toDrawable(context.resources)

        val pinLocationSymbol = PictureMarkerSymbol.createWithImage(pinImageBitmap)
        val pinSize = 50f
        //if (graphicList.isNotEmpty()) graphicList.removeLast()
        graphicsOverlays = mapView.graphicsOverlays
        removeLastPin()

        pinLocationSymbol.height = pinSize
        pinLocationSymbol.width = pinSize
        pinLocationSymbol.offsetY = pinSize / 2
        val pinLocationGraphic = Graphic(wgs84Point, pinLocationSymbol)

        graphicList.add(pinLocationGraphic)
        setGraphicsOverlay(mGraphicsOverlay)
        return wgs84Point
    }
}

fun removeLastPin() {
    if (graphicsOverlays.isNotEmpty()) graphicsOverlays.removeLast()

}