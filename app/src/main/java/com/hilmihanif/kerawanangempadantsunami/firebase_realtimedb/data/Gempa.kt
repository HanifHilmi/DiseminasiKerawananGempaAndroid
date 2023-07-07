package com.hilmihanif.kerawanangempadantsunami.firebase_realtimedb.data

import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference

data class Gempa(
    val Coordinates:String ="",
    val DateTime:String ="",
    val Jam:String = "",
    val Kedalaman:String ="",
    val Dirasakan:String ="",
    val Bujur:String ="",
    val Lintang:String ="",
    val Magnitude:String ="",
    val Potensi:String ="",
    val Tanggal:String ="",
    val Wilayah:String ="",
    val Shakemap:String ="",
    val Terkirim:String? = null
){
    fun getWgs84Point() : Point? {
        val inputString = Coordinates
        val values = inputString.split(",")
        if (values.size == 2) {
            val firstValue = values[0].toDoubleOrNull()
            val secondValue = values[1].toDoubleOrNull()
            return if (firstValue != null && secondValue != null) { Point(secondValue,firstValue, SpatialReference.wgs84(),) } else null
        }
        return null
    }

    fun getPoint():Point? = GeometryEngine.projectOrNull(getWgs84Point()!!,SpatialReference.webMercator())
//    Point(VIEWPOINT_X, VIEWPOINT_Y, SpatialReference.wgs84())

    fun getShakemapUrl():String = "https://data.bmkg.go.id/DataMKG/TEWS/$Shakemap"


}