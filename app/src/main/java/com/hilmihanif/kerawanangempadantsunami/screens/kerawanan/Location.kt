package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference

class Location(
    provinsi: String,
    lat:Double,
    long:Double,
) {
    var provinsi: String
        private set
    var point:Point
        private set
    var lat:Double
        private set
    var long:Double
        private set

    //fun getPoint():Point= point

    init {
        this.point = Point(lat,long, SpatialReference.wgs84())
        this.lat = lat
        this.long = long
        this.provinsi =provinsi
    }

    override fun toString(): String {
        return "lat :$lat,long: $long, Provinsi: $provinsi"
    }
}