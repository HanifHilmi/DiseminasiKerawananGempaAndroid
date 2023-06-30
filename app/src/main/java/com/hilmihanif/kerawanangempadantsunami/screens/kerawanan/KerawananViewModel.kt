package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan


import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.ApiKey
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.LocatorTask
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.mapTools.KerawananUrls
import com.hilmihanif.kerawanangempadantsunami.mapTools.addFaultModelLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.addKerawananGempaLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.addKerentananGerakanTanahLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.cekProvinsi
import com.hilmihanif.kerawanangempadantsunami.mapTools.removeLastPin
import com.hilmihanif.kerawanangempadantsunami.mapTools.reverseGeocoding
import com.hilmihanif.kerawanangempadantsunami.mapTools.setPin
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.pow

class KerawananViewModel(): ViewModel() {
    private lateinit var toggleList: List<String>


    private val _inputCardUiState = MutableStateFlow(InputCardUiState())
    private val _mapUiState= MutableStateFlow(MapUiState(setBaseMap()))
    private val _resultCardUiState = MutableStateFlow(ResultCardUiState())
    val inputCardUiState= _inputCardUiState.asStateFlow()
    val mapUiState= _mapUiState.asStateFlow()
    val resultCardUiState = _resultCardUiState.asStateFlow()




    fun setInputErrorAlert(errorPair:Pair<Boolean,String>?, durationSecond:Long? = null){
        viewModelScope.launch {

            if (errorPair != null){
                _inputCardUiState.update {
                    it.copy(currentErrorAlert = errorPair)
                }
            }else{
                _inputCardUiState.update {
                    it.copy(currentErrorAlert = false to "")
                }
            }
            if (durationSecond != null){
                delay(durationSecond*1000)
                _inputCardUiState.update {
                    it.copy(currentErrorAlert = false to "")
                }
            }
        }
    }



    fun setInitToggleState(list: List<String>) {
        toggleList = list

        if(_inputCardUiState.value.toggleButtonState.isEmpty()){
            _inputCardUiState.update {
                //if (locationEnabled){ } TODO(check if location isenabled)
                it.copy(toggleButtonState = toggleList[0])
            }
        }


        when(_inputCardUiState.value.toggleButtonState){
            toggleList.get(0)-> _inputCardUiState.update {
                it.copy(
                    //isTapPinEnabled = false,
                    toggleButtonState = toggleList[0]
                )
            }
            toggleList.get(1) -> _inputCardUiState.update {
                it.copy(
                    //isTapPinEnabled = true,
                    toggleButtonState = toggleList[1]
                )
            }
            toggleList.get(2)-> _inputCardUiState.update {
                it.copy(
                    //isTapPinEnabled = false,
                    toggleButtonState = toggleList[2]
                )
            }
        }
    }

    fun updateToggleState(select:String){
        if(select == toggleList[0]){
            removeLastPin()
            updateCurrentPinnedLocation()
        }else{
            updateCurrentPinnedLocation(_inputCardUiState.value.pinnedLocation)
        }
        _inputCardUiState.update {
            it.copy(
                toggleButtonState = select,
                currentErrorAlert = false to "",
            )
        }
    }

    fun updateMapDesc(mapStatus: State<LoadStatus>){
        val description = when (mapStatus.value) {
            LoadStatus.Loaded -> ""
            LoadStatus.Loading -> "Loading Map..."
            else -> "Failed to Load Map \n Check Connection"
        }
        _mapUiState.update {
            it.copy(
                currentMapStatusDesc = description
            )
        }
    }

    fun updateLocatorLoading(){
        _mapUiState.update {
            it.copy(
                locatorStatus =_mapUiState.value.locatorTask!!.loadStatus.value
            )
        }
    }

    fun setInputDesc(context:Context){
        var tempString: String
        var latlong = ""

        _inputCardUiState.value.let{
            if (it.pinnedLocation != null) {
                latlong = "Lat: ${String.format("%.4f",it.pinnedLocation.wgs84Point!!.y)},Long:${String.format("%.4f",it.pinnedLocation.wgs84Point!!.x)}"
//                latlong = "Lat: ${it.currentPinLocation.lat},Long:${it.currentPinLocation.long}"
            }

            tempString = when (_inputCardUiState.value.toggleButtonState) {
                toggleList[0] -> {
                    if (it.pinnedLocation == null) context.getString(R.string.input_location,"Test") else "Lokasi Anda Saat ini ${it.pinnedLocation.provinsi}\n" + latlong
                }
                toggleList[1] -> {
                    if (it.pinnedLocation == null) context.getString(R.string.input_map) else "Pin Location ${it.pinnedLocation.provinsi}\n" + latlong
                }
                else -> {
                    if (it.pinnedLocation == null) context.getString(R.string.input_koordinat) else "Koordinat yang di input ${it.pinnedLocation.provinsi}\n" + latlong
                }
            }
        }
        _inputCardUiState.update {
            it.copy(
                currentInputDesc = tempString
            )
        }
    }

    fun setOnInputPin(point:Point?,mapView: MapView) {
        viewModelScope.launch {
            val result = reverseGeocoding(point!!, mapView, _mapUiState.value.locatorTask!!)
            val cekProv = cekProvinsi(result)

            if (cekProv.second) {
                _mapUiState.value.currentPinLocation?.setPinAllowed()
            }
        }
    }

    fun setOnTapPinLocation(point: Point?, mapView: MapView,toggleList: List<String>) {
        if (_mapUiState.value.locatorTask != null){
            viewModelScope.launch {
                val result = reverseGeocoding(point!!,mapView,_mapUiState.value.locatorTask!!)
                val cekProv = cekProvinsi(result)
//            _mapUiState.update {
//                it.copy(isTapPinEnabled = cekProv.second,)
//            }

                _mapUiState.value.let{ value->
                    //updateCurrentGeocode(point,mapView, locatorTask)
                    Log.d(TEST_LOG,"reverse geocode when tap allowed:${value.isInputProcessNotDone} result: $result")
                    Log.d(TEST_LOG,"point spatial ref:${point.spatialReference} result: $point")

                    updateLocatorLoading()
                    if (value.isInputProcessNotDone){
                        if (cekProv.second){
                            updateToggleState( toggleList[1])
                            val wgsPoint = setPin(
                                mapView = mapView,
                                mapPoint = point
                            ){
                                setInputErrorAlert(null)
                                if (mapView.graphicsOverlays.isNotEmpty()) mapView.graphicsOverlays.removeLast()
                                mapView.graphicsOverlays.add(it)

                            }
                            val location = Location(cekProv.first,point = point, wgs84Point = wgsPoint)
                            updateCurrentPinnedLocation(location)

                        }else{
                            setInputErrorAlert(
                                true to "Diluar Jangkauan Penentuan",5
                            )
                        }

                    }
                }

            }

        }

    }

    private fun updateCurrentPinnedLocation(location :Location? = null){
        if (location != null){
            _mapUiState.update {
                it.copy(
                    currentPinLocation = location,
                    currentViewPoint = Viewpoint(location.wgs84Point!! /*offsetYPoint()*/)
                )
            }
            _inputCardUiState.update {
                it.copy(
                    pinnedLocation = location,
                    latTextFieldValue = TextFieldValue(String.format("%.4f",location.lat)),
                    longTextFieldValue = TextFieldValue(String.format("%.4f",location.long)),
                )
            }
            _resultCardUiState.update {
                it.copy(
                    currentProvinsi = location.provinsi
                )
            }
        }else{
            _mapUiState.update {
                it.copy(
                    currentPinLocation = null
                )
            }
            _inputCardUiState.update {
                it.copy(
                    pinnedLocation = null,
                    latTextFieldValue = TextFieldValue(""),
                    longTextFieldValue = TextFieldValue(""),
                )
            }
            _resultCardUiState.update {
                it.copy(
                    currentProvinsi = ""
                )
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

        if(_mapUiState.value.locatorTask == null){
            _mapUiState.update {
                it.copy(locatorTask = locatorTask)
            }
        }
    }

    fun setLatLongFieldValue(lat: TextFieldValue?=null, long:TextFieldValue?=null) {
        when{
            lat != null -> _inputCardUiState.update {
                it.copy(latTextFieldValue = lat)
            }
            long != null -> _inputCardUiState.update {
                it.copy(longTextFieldValue = long)
            }
        }
    }

    private fun setFaultLayer(){
        addFaultModelLayer(_mapUiState.value.map)
    }

    private fun setKRBGempaLayer(url: String){
        viewModelScope.launch {
           val status = addKerawananGempaLayer(_mapUiState.value.map,url)
            _resultCardUiState.update {
                it.copy(
                    gempaLoadStatus = status
                )
            }
        }
    }
    private fun setZkgtLayer(url: String){
        viewModelScope.launch {
           val status = addKerentananGerakanTanahLayer(_mapUiState.value.map,url)
            _resultCardUiState.update {
                it.copy(
                    gmLoadStatus = status
                )
            }
        }
    }

    fun updateLayerLoadStatus(){
        when (_mapUiState.value.totalKRBLayerCount){
            1 -> {
                _resultCardUiState.update { it.copy(isLayerLoaded = (_resultCardUiState.value.gempaLoadStatus.value == LoadStatus.Loaded)) }
            }
            2 ->{
                val check = (_resultCardUiState.value.gempaLoadStatus.value == LoadStatus.Loaded) && (_resultCardUiState.value.gmLoadStatus.value == LoadStatus.Loaded)
                _resultCardUiState.update { it.copy(isLayerLoaded = check) }
            }
        }
    }


    fun setOnProcessButtonClicked(prov :String) {
        val urlGempa = KerawananUrls.gempa.getValue(prov)
        val urlGM = KerawananUrls.gerakanTanah.getValue(prov)
        val urlTsunami = KerawananUrls.tsunami.getValue(prov)
        var count =0
        if(urlGempa.isNotEmpty()) { setKRBGempaLayer(urlGempa) ;count++ }
        if(urlGM.isNotEmpty()) { setZkgtLayer(urlGM) ;count++}
        if(urlTsunami.isNotEmpty()) {/*TODO(set Tsunami layer)*/count++}

        _mapUiState.update {
            it.copy(
                isInputProcessNotDone = false,
                totalKRBLayerCount = count
            )
        }






    }

    fun setLayerLoadingStatus() {
        TODO("Not yet implemented")
    }


    init {
        setFaultLayer()
        setLocatorTask()
    }
}

data class InputCardUiState(
    val pinnedLocation: Location? = null,
    val toggleButtonState:String = "",
    val currentErrorAlert: Pair<Boolean,String> = false to "",
    val currentInputDesc:String = "",
    val currentReverseGeocoding: Map<String, Any?> = mapOf(),
    val latTextFieldValue: TextFieldValue = TextFieldValue(""),
    val longTextFieldValue: TextFieldValue = TextFieldValue("")
)

data class ResultCardUiState(
    val selectedProv :String = "",
    val isLayerLoaded :Boolean = false,
    val gempaLoadStatus: StateFlow<LoadStatus> = MutableStateFlow(LoadStatus.NotLoaded),
    val gmLoadStatus: StateFlow<LoadStatus> = MutableStateFlow(LoadStatus.NotLoaded),
    val tsuLoadStatus: StateFlow<LoadStatus> = MutableStateFlow(LoadStatus.NotLoaded),
    val currentProvinsi :String = "",
)

data class MapUiState(
    val map:ArcGISMap,
    val currentGeocodeResult: Map<String,Any?> = mapOf(),
    val currentPinLocation: Location? = null,
    val isInputProcessNotDone:Boolean = true,
    val locatorStatus :LoadStatus = LoadStatus.NotLoaded,
    val locatorTask: LocatorTask? = null,
    val currentViewPoint : Viewpoint = Viewpoint(3.028, 98.905, 7000000.0),
    val currentMapStatusDesc: String = "",
    val totalKRBLayerCount:Int = 0

    )


