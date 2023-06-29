package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import com.arcgismaps.geometry.GeometryEngine
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.SpatialReference

class Location(
    provinsi: String,
    wgs84Point :Point? = null,
    point :Point? = null,
) {
    var provinsi: String
        private set
    var point:Point
        private set
    var lat:Double
        private set
    var long:Double
        private set

    var isPinAllowed:Boolean
        private set

    var wgs84Point: Point?
        private set

    //fun getPoint():Point= point

    init {
        when{
            (point == null) ->{
                this.wgs84Point = wgs84Point
                this.point = GeometryEngine.projectOrNull(wgs84Point!!,SpatialReference.webMercator())!!
                this.lat = this.wgs84Point!!.y
                this.long = this.wgs84Point!!.x
            }
            (wgs84Point == null) ->{
                this.point = point
                this.wgs84Point = GeometryEngine.projectOrNull(point,SpatialReference.webMercator())!!
                this.lat = this.wgs84Point!!.y
                this.long = this.wgs84Point!!.x
            }
            else->{
                this.point = point
                this.wgs84Point =wgs84Point
                this.lat = this.wgs84Point!!.y
                this.long = this.wgs84Point!!.x
            }
        }



        this.provinsi =provinsi
        this.isPinAllowed = false
    }

    override fun toString(): String {
        return "lat :$lat,long: $long, Provinsi: $provinsi"
    }

    fun setPinAllowed(){
        isPinAllowed = !isPinAllowed
    }


}