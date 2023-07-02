package com.hilmihanif.kerawanangempadantsunami.mapTools

import android.util.Log
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.view.MapView
import com.hilmihanif.kerawanangempadantsunami.utils.GEMPA_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.GM_LAYER_INDEX
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import com.hilmihanif.kerawanangempadantsunami.utils.TSUNAMI_LAYER_INDEX
import kotlinx.coroutines.delay

suspend fun identifyKerawananLayers(mapView:MapView,mapPoint: Point,layersCount:Int): List<Map<String, Any?>>{
    val mutableList = mutableListOf<Map<String,Any?>>()
    val scrPoint = mapView.locationToScreen(mapPoint)

    delay(200)

    mapView.map!!.operationalLayers.let {
        while (it.size-1 < layersCount){
            delay(100)
            Log.d(TEST_LOG,"Only 1 layers detected maplayersize${it.size}, layerscount:${layersCount}")
        }
        if (it.size >= 2) {
            Log.d(TEST_LOG,"2nd layers detected")
            do{
                val identifyGempaLayerResult = mapView.identifyLayer(
                    it[GEMPA_LAYER_INDEX],
                    scrPoint,
                    10.0,
                    false,
                    1
                ).getOrNull()
                Log.d(TEST_LOG,"identify Gempa Layer Result $identifyGempaLayerResult :${identifyGempaLayerResult!!.geoElements}")
                if (identifyGempaLayerResult.geoElements.isNotEmpty()){
                    for (layerResult in identifyGempaLayerResult.geoElements){
                        mutableList.add(layerResult.attributes)
                        Log.d(TEST_LOG,"mutableList ${mutableList.size} :${mutableList}")
                    }
                    break
                }
                delay(200)
            }while (true)
            if (it.size == 3) {
                Log.d(TEST_LOG,"3rd layers detected")
                do{
                    val identifyGMLayerResult = mapView.identifyLayer(
                        it[GM_LAYER_INDEX],
                        scrPoint,
                        10.0,
                        false,
                        1
                    ).getOrNull()
                    Log.d(TEST_LOG,"identify GM Layer Result $identifyGMLayerResult :${identifyGMLayerResult!!.geoElements}")
                    if (identifyGMLayerResult.geoElements.isNotEmpty()){
                        for (layerResult in identifyGMLayerResult.geoElements){
                            mutableList.add(layerResult.attributes)
                            Log.d(TEST_LOG,"mutableList ${mutableList.size} :${mutableList}")
                        }
                        break
                    }
                    delay(200)
                }while (true)
                //mutableList.add(identifyGMLayerResult!!.geoElements[0].attributes)
            }
            if (it.size == 4) {
                Log.d(TEST_LOG,"4th layers detected")
                do{
                    val identifyTsuLayerResult = mapView.identifyLayer(
                        it[TSUNAMI_LAYER_INDEX],
                        scrPoint,
                        10.0,
                        false,
                        1
                    ).getOrNull()
                    Log.d(TEST_LOG,"identify Tsu Layer Result $identifyTsuLayerResult :${identifyTsuLayerResult!!.geoElements}")
                    if (identifyTsuLayerResult.geoElements.isNotEmpty()){
                        for (layerResult in identifyTsuLayerResult.geoElements){
                            mutableList.add(layerResult.attributes)
                            Log.d(TEST_LOG,"mutableList ${mutableList.size} :${mutableList}")
                        }
                        break
                    }
                    delay(500)
                }while (true)
                //mutableList.add(identifyTsuLayerResult!!.geoElements[0].attributes)
            }
        }

        Log.d(
            TEST_LOG,
            "in identifykerawanan layer size ${mapView.map!!.operationalLayers.size}"
        )
        Log.d(
            TEST_LOG,
            "mutable list size ${mutableList.size}"
        )
        return mutableList
    }
}
