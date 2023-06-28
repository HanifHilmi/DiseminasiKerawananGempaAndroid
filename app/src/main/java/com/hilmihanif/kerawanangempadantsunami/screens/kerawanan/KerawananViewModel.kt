package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan


import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.ApiKey
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.mapTools.cekProvinsi
import com.hilmihanif.kerawanangempadantsunami.mapTools.reverseGeocoding
import com.hilmihanif.kerawanangempadantsunami.mapTools.setPin
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow

class KerawananViewModel(): ViewModel() {
    private lateinit var toggleList: List<String>


    private val _kerawananUiState = MutableStateFlow(KerawananUiState())


    val kerawananUiState:StateFlow<KerawananUiState> = _kerawananUiState.asStateFlow()


//    fun checkPinEnabled():Boolean{

    fun updateErrorAlert(errorPair:Pair<Boolean,String>){
        _kerawananUiState.update {
            it.copy(currentErrorAlert = errorPair)
        }
    }

    //    }
//        }
//            else -> false
//            is "" -> true
//        return when(_kerawananUiState.value.toggleButtonState){


    fun setInitToggleState(list: List<String>) {
        toggleList = list

        if(_kerawananUiState.value.toggleButtonState.isEmpty()){
            _kerawananUiState.update {
                //if (locationEnabled){ } TODO(check if location isenabled)
                it.copy(toggleButtonState = toggleList[0])
            }
        }


        when(_kerawananUiState.value.toggleButtonState){
            toggleList.get(0)-> _kerawananUiState.update {
                it.copy(
                    isPinEnabled = false,
                    toggleButtonState = toggleList.get(0)
                )
            }
            toggleList.get(1) -> _kerawananUiState.update {
                it.copy(
                    isPinEnabled = true,
                    toggleButtonState = toggleList.get(1)
                )
            }
            toggleList.get(2)-> _kerawananUiState.update {
                it.copy(
                    isPinEnabled = false,
                    toggleButtonState = toggleList.get(2)
                )
            }
        }
    }

    fun updateToggleState(select:String){
        _kerawananUiState.update {
            it.copy(
                toggleButtonState = select,
                currentPinLocation = null,
                currentErrorAlert = false to ""
            )

        }
    }

    fun updateMapDesc(mapStatus: State<LoadStatus>){
        val description = when (mapStatus.value) {
            LoadStatus.Loaded -> ""
            LoadStatus.Loading -> "Loading Map..."
            else -> "Failed to Load Map \n Check Connection"
        }
        _kerawananUiState.update {
            it.copy(
                currentMapStatusDesc = description
            )
        }
    }

    fun updateLocatorLoading(){
        _kerawananUiState.update {
            it.copy(locatorStatus =_kerawananUiState.value.locatorTask!!.loadStatus.value )

        }
    }

    fun setInputDesc(context:Context){
        var tempString = ""
        var latlong = ""

        _kerawananUiState.value.let{
            if (it.currentPinLocation != null) {
                latlong = "Lat: ${String.format("%.4f",it.currentPinLocation.lat)},Long:${String.format("%.4f",it.currentPinLocation.long)}"
//                latlong = "Lat: ${it.currentPinLocation.lat},Long:${it.currentPinLocation.long}"
            }

            tempString = when (it.toggleButtonState) {
                toggleList[0] -> {
                    if (it.currentPinLocation == null) context.getString(R.string.input_location) else "Lokasi Anda Saat ini ${it.currentPinLocation.provinsi}\n" + latlong
                }
                toggleList[1] -> {
                    if (it.currentPinLocation == null) context.getString(R.string.input_map) else "Pin Location ${it.currentPinLocation.provinsi}\n" + latlong
                }
                else -> {
                    if (it.currentPinLocation == null) context.getString(R.string.input_koordinat) else "Koordinat yang di input ${it.currentPinLocation.provinsi}\n" + latlong
                }
            }
        }
        _kerawananUiState.update {
            it.copy(
                currentInputDesc = tempString
            )
        }
    }

    fun setOnTapPinLocation(point: Point?, mapView: MapView,toggleList: List<String>) {
        viewModelScope.launch {
            val result = reverseGeocoding(point!!,mapView,_kerawananUiState.value.locatorTask!!)
            val cekProv = cekProvinsi(result)
            _kerawananUiState.update {
                it.copy(isPinEnabled = cekProv.second,)
            }

            _kerawananUiState.value.let{value->
                //updateCurrentGeocode(point,mapView, locatorTask)
                Log.d(TEST_LOG,"reverse geocode when tap allowed:${value.isPinEnabled} result: $result")
                Log.d(TEST_LOG,"point spatial ref:${point.spatialReference} result: $point")

                updateLocatorLoading()
                if (value.isPinEnabled){
                    updateToggleState( toggleList[1])
                    val wgs84 = setPin(
                        mapView = mapView,
                        mapPoint = point
                    ){
                        updateErrorAlert(false to "")
                        if (mapView.graphicsOverlays.isNotEmpty()) mapView.graphicsOverlays.removeLast()
                        mapView.graphicsOverlays.add(it)

                    }

                    _kerawananUiState.update {currentState->
                        currentState.copy(
                            currentPinLocation = Location(cekProv.first,wgs84!!.y,wgs84.x),
                            currentViewPoint = Viewpoint(point /*offsetYPoint()*/)
                        )
                    }


                }else{
                    updateErrorAlert(
                        true to "Diluar Jangkauan Penentuan"
                    )
                }
            }

        }

    }

    private fun offsetYPoint(point:Point,offset:Double = -2.0 ):Point{
        return Point(
            point.x,
            point.y + offset * 10.0.pow(5.0)
            , point.spatialReference
        )

    }

    fun setLocatorTask(){
        val locatorTask = LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer").apply {
            this.apiKey = ApiKey.create(BuildConfig.API_KEY)
        }
        viewModelScope.launch {
            Log.d(TEST_LOG," load locator in setbasemap() before ${locatorTask.loadStatus.value}")
            locatorTask.load()
            Log.d(TEST_LOG," load locator in setbasemap() after ${locatorTask.loadStatus.value}")
        }

        if(kerawananUiState.value.locatorTask == null){
            _kerawananUiState.update {
                it.copy(locatorTask = locatorTask)
            }
        }
    }


    init {
        setLocatorTask()
    }
}

data class KerawananUiState(
    val toggleButtonState:String = "",
    val isPinEnabled:Boolean = false,
    val locatorStatus :LoadStatus = LoadStatus.NotLoaded,
    val currentPinLocation: Location? = null,
    val currentViewPoint : Viewpoint = Viewpoint(3.028, 98.905, 7000000.0),
    val currentErrorAlert: Pair<Boolean,String> = false to "",
    val currentMapStatusDesc: String = "",
    val currentInputDesc:String = "",
    val currentReverseGeocoding: Map<String, Any?> = mapOf(),
    val locatorTask: LocatorTask? = null
)

