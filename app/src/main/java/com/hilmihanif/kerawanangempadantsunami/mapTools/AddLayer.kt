package com.hilmihanif.kerawanangempadantsunami.mapTools

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.arcgismaps.Color
import com.arcgismaps.LoadStatus
import com.arcgismaps.data.ServiceFeatureTable
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.layers.FeatureLayer
import com.arcgismaps.mapping.symbology.ClassBreak
import com.arcgismaps.mapping.symbology.ClassBreaksRenderer
import com.arcgismaps.mapping.symbology.SimpleLineSymbol
import com.arcgismaps.mapping.symbology.SimpleLineSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleRenderer
import com.arcgismaps.mapping.symbology.UniqueValue
import com.arcgismaps.mapping.symbology.UniqueValueRenderer
import com.hilmihanif.kerawanangempadantsunami.ui.theme.FaultColor
import com.hilmihanif.kerawanangempadantsunami.utils.FAULT_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.GEMPA_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.GM_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import com.hilmihanif.kerawanangempadantsunami.utils.TSUNAMI_LAYER_INDEX
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow


fun addFaultModelLayer(baseMap: ArcGISMap) {
    val faultModelLayer =
        FeatureLayer.createWithFeatureTable(ServiceFeatureTable("https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/patahan_tektonik_fixed/FeatureServer/0"))



    val lineSymbol = SimpleLineSymbol(
        SimpleLineSymbolStyle.Solid,
        Color(FaultColor.toArgb()),
        2.0f
    )



    val renderer = SimpleRenderer(lineSymbol)
    faultModelLayer.renderer = renderer
    faultModelLayer.opacity = 0.75f


    baseMap.operationalLayers.add(FAULT_LAYER_INDEX,faultModelLayer)
    Log.d(TEST_LOG,"fault layer successfully added")


}


suspend fun addSeismisitasLayer(baseMap: ArcGISMap,layerCount:Int) {
    val seismisitas2022Layer =
        FeatureLayer.createWithFeatureTable(ServiceFeatureTable("https://services7.arcgis.com/5U3WUC2hg7PzozWK/arcgis/rest/services/seismisitas_sumatera_2022/FeatureServer/0"))

    val pointSymbolMin = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle,Color.red,5f)
    val pointSymbolMid = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle,Color.red,10f)
    val pointSymbolMax = SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Circle,Color.red,15f)


    val classBreakValueMin = ClassBreak("Magnitude","M<=4",0.0,3.9999999,pointSymbolMin)
    val classBreakValueMid = ClassBreak("Magnitude","4 < M <= 5",4.0,4.9999999,pointSymbolMid)
    val classBreakValueMax = ClassBreak("Seismisitas","M>5",5.0,10.0, pointSymbolMax)



    val classBreakRenderer = ClassBreaksRenderer("Magnitude", listOf(classBreakValueMin,classBreakValueMid,classBreakValueMax))
    seismisitas2022Layer.renderer = classBreakRenderer
//    while (baseMap.operationalLayers.size >= layerCount){
//        delay(100)
//    }
    baseMap.operationalLayers.add(seismisitas2022Layer)
}



@SuppressLint("ClickableViewAccessibility")
suspend fun addKerawananGempaLayer(baseMap: ArcGISMap, url: String):StateFlow<LoadStatus> {

    val gempaLayer :FeatureLayer = FeatureLayer.createWithFeatureTable(ServiceFeatureTable(url))

    while (gempaLayer.loadStatus.value != LoadStatus.Loaded){
        delay(200)
        Log.d(TEST_LOG,"gempa layer loadStatus : ${gempaLayer.loadStatus.value}")
        when {
            gempaLayer.loadStatus.value == LoadStatus.Loading -> {continue }
            gempaLayer.loadStatus.value != LoadStatus.Loaded -> {
                gempaLayer.retryLoad()
            }
        }
    }

    val sangatRendah = UniqueValue(
        "Kerawanan Gempa", "Sangat Rendah", KerawananSymbol.sangatRendah(), listOf("205")
    )
    val rendah = UniqueValue(
        "Kerawanan Gempa", "Rendah", KerawananSymbol.rendah(), listOf("204")
    )
    val sedang = UniqueValue(
        "Kerawanan Gempa", "Sedang", KerawananSymbol.menengah(), listOf("203")
    )
    val tinggi = UniqueValue(
        "Kerawanan Gempa", "Tinggi", KerawananSymbol.tinggi(), listOf("202")
    )

    val uniqueValueList = listOf(sangatRendah, rendah, sedang, tinggi)

    val fieldNames = listOf("KRBID")

    val renderer = UniqueValueRenderer(fieldNames, uniqueValueList)
    gempaLayer.let {
        it.renderer = renderer
        it.opacity = 0.5f
        baseMap.operationalLayers.add(GEMPA_LAYER_INDEX,it)

        return it.loadStatus

    }
//        mMapView.setViewpointAnimated(Viewpoint(3.028, 98.905,10000000.0),1f, AnimationCurve.EaseInCirc)

}

@SuppressLint("ClickableViewAccessibility")
suspend fun addKerentananGerakanTanahLayer(baseMap: ArcGISMap,url:String): StateFlow<LoadStatus>  {
    val gerakanTanahLayer : FeatureLayer = FeatureLayer.createWithFeatureTable(ServiceFeatureTable(url))

        while (gerakanTanahLayer.loadStatus.value != LoadStatus.Loaded){
            delay(200)
            Log.d(TEST_LOG,"gm layer loadStatus : ${gerakanTanahLayer.loadStatus.value}")
            if (gerakanTanahLayer.loadStatus.value == LoadStatus.Loading)continue
            else if(gerakanTanahLayer.loadStatus.value != LoadStatus.Loaded){
                gerakanTanahLayer.retryLoad()
            }
        }
        Log.d(TEST_LOG,"layers count: ${baseMap.operationalLayers.size}")



    val sangatRendah = UniqueValue(
        "Zona Kerentanan Gerakan Tanah", "Sangat Rendah", KerawananSymbol.sangatRendah(), listOf("Zona Kerentanan Gerakan Tanah Sangat Rendah")
    )
    val rendah = UniqueValue(
        "Zona Kerentanan Gerakan Tanah", "Rendah", KerawananSymbol.rendah(), listOf("Zona Kerentanan Gerakan Tanah Rendah")
    )
    val sedang = UniqueValue(
        "Zona Kerentanan Gerakan Tanah", "Sedang", KerawananSymbol.menengah(), listOf("Zona Kerentanan Gerakan Tanah Menengah")
    )
    val tinggi = UniqueValue(
        "Zona Kerentanan Gerakan Tanah", "Tinggi", KerawananSymbol.tinggi(), listOf("Zona Kerentanan Gerakan Tanah Tinggi")
    )
    val rombakan = UniqueValue(
        "Zona Kerentanan Gerakan Tanah","Alur Rentan Aliran Bahan Rombakan",KerawananSymbol.rombakan(), listOf("Alur Aliran Bahan Rombakan")
    )


    val uniqueValueList = listOf(sangatRendah, rendah, sedang, tinggi,rombakan)

    val fieldNames = listOf("NAMOBJ")
    val renderer = UniqueValueRenderer(fieldNames, uniqueValueList)

    gerakanTanahLayer.let {
        it.renderer = renderer
        it.opacity = 0.5f
        while (baseMap.operationalLayers.size < GM_LAYER_INDEX){
            delay(200)
        }
        baseMap.operationalLayers.add(GM_LAYER_INDEX,it)

        return it.loadStatus
    }
}

suspend fun addKerawananTsunamiLayer(baseMap: ArcGISMap,url: String): StateFlow<LoadStatus> {
    val tsunamiLayer : FeatureLayer = FeatureLayer.createWithFeatureTable(ServiceFeatureTable(url))

    while (tsunamiLayer.loadStatus.value != LoadStatus.Loaded){
        delay(200)
        Log.d(TEST_LOG,"tsunami layer loadStatus : ${tsunamiLayer.loadStatus.value}")
        if (tsunamiLayer.loadStatus.value == LoadStatus.Loading)continue
        else if(tsunamiLayer.loadStatus.value != LoadStatus.Loaded){
            tsunamiLayer.retryLoad()
        }
    }
    Log.d(TEST_LOG,"layers count: ${baseMap.operationalLayers.size}")



    val sangatRendah = UniqueValue(
        "Kerawanan Tsunami", "Sangat Rendah", KerawananSymbol.sangatRendah(), listOf("104")
    )
    val rendah = UniqueValue(
        "Kerawanan Tsunami", "Rendah", KerawananSymbol.rendah(), listOf("103")
    )
    val sedang = UniqueValue(
        "Kerawanan Tsunami", "Sedang", KerawananSymbol.menengah(), listOf("102")
    )
    val tinggi = UniqueValue(
        "Kerawanan Tsunami", "Tinggi", KerawananSymbol.tinggi(), listOf("101")
    )


    val uniqueValueList = listOf(sangatRendah, rendah, sedang, tinggi)

    val fieldNames = listOf("ID")
    val renderer = UniqueValueRenderer(fieldNames, uniqueValueList)

    tsunamiLayer.let {
        it.renderer = renderer
        it.opacity = 0.5f
        while (baseMap.operationalLayers.size < TSUNAMI_LAYER_INDEX){
            delay(200)
        }
        baseMap.operationalLayers.add(TSUNAMI_LAYER_INDEX,it)

        return it.loadStatus
    }
}



