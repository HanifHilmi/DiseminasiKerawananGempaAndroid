package com.hilmihanif.kerawanangempadantsunami.viewmodels


import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.ApiKey
import com.arcgismaps.LoadStatus
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.LocationDisplay
import com.arcgismaps.mapping.view.MapView
import com.arcgismaps.tasks.geocode.LocatorTask
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hilmihanif.kerawanangempadantsunami.BuildConfig
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.DataState
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.Gempa
import com.hilmihanif.kerawanangempadantsunami.mapTools.KerawananUrls
import com.hilmihanif.kerawanangempadantsunami.mapTools.Location
import com.hilmihanif.kerawanangempadantsunami.mapTools.addFaultModelLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.addKerawananGempaLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.addKerawananTsunamiLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.addKerentananGerakanTanahLayer
import com.hilmihanif.kerawanangempadantsunami.mapTools.cekProvinsi
import com.hilmihanif.kerawanangempadantsunami.mapTools.identifyKerawananLayers
import com.hilmihanif.kerawanangempadantsunami.mapTools.removeLastPin
import com.hilmihanif.kerawanangempadantsunami.mapTools.reverseGeocoding
import com.hilmihanif.kerawanangempadantsunami.mapTools.setBaseMap
import com.hilmihanif.kerawanangempadantsunami.mapTools.setGempaPin
import com.hilmihanif.kerawanangempadantsunami.mapTools.setPin
import com.hilmihanif.kerawanangempadantsunami.utils.FIREBASE_TEST
import com.hilmihanif.kerawanangempadantsunami.utils.MAP_MAX_SCALE
import com.hilmihanif.kerawanangempadantsunami.utils.TEST_LOG
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
class MainMapViewModel : ViewModel() {
    private lateinit var toggleList: List<String>


    private val _inputCardUiState = MutableStateFlow(InputCardUiState())
    private val _mapUiState= MutableStateFlow(MapUiState(setBaseMap()))
    private val _resultCardUiState = MutableStateFlow(ResultCardUiState())
    private val _firebaseResponse: MutableStateFlow<DataState> = MutableStateFlow(DataState.Empty)

    private val _mapView = MutableLiveData<MapView>()
    private val _locationDisplay = MutableLiveData<LocationDisplay>()
    private val _mapScale = MutableStateFlow<Double>(0.0)
    private val _latestResponse = MutableStateFlow(Gempa())


    val inputCardUiState= _inputCardUiState.asStateFlow()
    val mapUiState= _mapUiState.asStateFlow()

    val mapScale = _mapScale.asStateFlow()
    val firebaseResponse = _firebaseResponse.asStateFlow()
    val latestResponse = _latestResponse.asStateFlow()


    val resultCardUiState = _resultCardUiState.asStateFlow()




    private fun setInputErrorAlert(errorPair:Pair<Boolean,String>?, durationSecond:Long? = null){
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



    @OptIn(ExperimentalPermissionsApi::class)
    fun setInitToggleState(list: List<String>, locationPermissionState: MultiplePermissionsState) {
        toggleList = list

        Log.d(TEST_LOG,"state toggle ${_inputCardUiState.value.toggleButtonState.isEmpty()} ${_inputCardUiState.value.toggleButtonState}")
        if(_inputCardUiState.value.toggleButtonState.isEmpty()){
            _inputCardUiState.update {
                if (locationPermissionState.allPermissionsGranted){
                    updateToggleState(list[0],locationPermissionState)
                    Log.d(TEST_LOG,"update toggle ${_inputCardUiState.value.toggleButtonState.isEmpty()} ${_inputCardUiState.value.toggleButtonState}")

                    it.copy(toggleButtonState = toggleList[0]) //TODO(check if location isenabled)
                } else{
                    it.copy(toggleButtonState = toggleList[1])
                }
            }

        }

        when(_inputCardUiState.value.toggleButtonState){
            toggleList[0] -> _inputCardUiState.update {
                it.copy(
                    toggleButtonState = toggleList[0]
                )
            }
            toggleList[1] -> _inputCardUiState.update {
                it.copy(
                    toggleButtonState = toggleList[1]
                )
            }
            toggleList[2] -> _inputCardUiState.update {
                it.copy(
                    toggleButtonState = toggleList[2]
                )
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun updateToggleState(select:String, multiplePermissionsState: MultiplePermissionsState? = null){

        _inputCardUiState.update {
            it.copy(
                toggleButtonState = select,
                currentErrorAlert = false to "",
            )
        }
        if(select == toggleList[0]){
            try {
                removeLastPin()
            }catch (e:Exception){
                Log.d(TEST_LOG,e.message.toString())
            }
            //updateCurrentPinnedLocation()
            viewModelScope.launch {

                if (multiplePermissionsState?.allPermissionsGranted == true){
                    if (_locationDisplay.isInitialized){
                        _locationDisplay.value?.let {locationDisplay->
                            viewModelScope.launch {
                                locationDisplay.dataSource.start().onSuccess {
                                    Log.d(TEST_LOG,"location display succes")

                                    var currentposition :Point?
                                    do {
                                        currentposition = locationDisplay.location.value?.position
                                        _mapView.value?.let {
                                            setOnTapPinLocation(currentposition, it, false)
                                        }
                                        delay(200)
                                    } while (currentposition == null)

                                    Log.d(TEST_LOG,"location x:${currentposition?.x} y:${currentposition?.y} ")
                                }.onFailure {
                                    Log.d(TEST_LOG,"location display failed ${it.message}")
                                }
                            }
                        }
                    }
                } else {
                    _inputCardUiState.update {
                        it.copy(
                            currentInputDesc = "Lokasi Tidak Diaktifkan, silahkan gunakan opsi lain",
                            isLocationDisabled = true
                        )
                    }
                    setInputErrorAlert(true to "Lokasi tidak diaktifkan",5)
                }
            }


        }else{
            updateCurrentPinnedLocation(_inputCardUiState.value.pinnedLocation)
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

    fun removeAllGempaPin(){
        if(_mapView.isInitialized){

            _mapView.value?.let{mapView ->
                mapView.graphicsOverlays.clear()
            }
        }
    }

    fun setOnGempaPin(gempa: Gempa){
        if (_mapView.isInitialized){
            viewModelScope.launch {
                while (_mapView.value == null){
                    delay(200)
                }
                _mapView.value?.let {mapView->
                    setGempaPin(wgs84Point = gempa.getWgs84Point(), mapView = mapView){
                        if (mapView.graphicsOverlays.isNotEmpty()) mapView.graphicsOverlays.removeLast()
                        mapView.graphicsOverlays.add(it)
                    }
                }
                if(gempa.getPoint() != null){
                    _mapUiState.update {
                        it.copy(
                            currentViewPoint = Viewpoint(gempa.getPoint()!!)
                        )
                    }
                }

            }

        }
    }

    /*
    @OptIn(ExperimentalPermissionsApi::class)
    suspend fun setCurrentUserPinLocation(mapView: MapView?, point: Point?){

        if (_mapUiState.value.locatorTask != null){
            val result = reverseGeocoding(point!!,mapView,_mapUiState.value.locatorTask!!)
            val cekProv = cekProvinsi(result)
        }


    }

     */

    @OptIn(ExperimentalPermissionsApi::class)
    fun setOnTapPinLocation(point: Point?, mapView: MapView,istapped:Boolean) {

        if (_mapUiState.value.locatorTask != null && point != null){
            viewModelScope.launch {
                val result = reverseGeocoding(point,mapView,_mapUiState.value.locatorTask!!)
                val cekProv = cekProvinsi(result)

                _mapUiState.value.let{ value->
                    Log.d(TEST_LOG,"reverse geocode when tap allowed:${value.isInputProcessNotDone} result: $result")
                    Log.d(TEST_LOG,"point spatial ref:${point?.spatialReference} result: $point")

                    updateLocatorLoading()
                    if (value.isInputProcessNotDone){
                        if (cekProv.second){
                            if (istapped)updateToggleState(toggleList[1])
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
                            if(istapped){
                                setInputErrorAlert(
                                    true to "Diluar Jangkauan Penentuan",5
                                )
                            }else{
                                setInputErrorAlert(
                                    true to "Lokasi Anda diluar jangkauan",5
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateCurrentPinnedLocation(location : Location? = null){
        if (location != null){
            _mapUiState.update {
                it.copy(
                    currentPinLocation = location,
                    currentViewPoint = Viewpoint(offsetYPoint(location.wgs84Point!!,1.5))
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

    private fun offsetYPoint(point:Point,offset:Double):Point{
        return Point(
            point.x,
            point.y - offset
            //point.y + offset * 10.0.pow(5.0)
            , point.spatialReference
        )

    }



    private fun setLocatorTask(){
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

    private fun setKRBtsunamiLayer(url: String){
        viewModelScope.launch {
            val status = addKerawananTsunamiLayer(_mapUiState.value.map,url)
            _resultCardUiState.update {
                it.copy(
                    tsuLoadStatus = status
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
            3 ->{
                val check = (_resultCardUiState.value.gempaLoadStatus.value == LoadStatus.Loaded) && (_resultCardUiState.value.gmLoadStatus.value == LoadStatus.Loaded) && (_resultCardUiState.value.tsuLoadStatus.value == LoadStatus.Loaded)
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
        if(urlTsunami.isNotEmpty()) { setKRBtsunamiLayer(urlTsunami);count++}

        _mapUiState.update {
            it.copy(
                isInputProcessNotDone = false,
                totalKRBLayerCount = count
            )
        }
        identifyLayers()
    }


    private fun identifyLayers(){
        viewModelScope.launch {
            if (_mapView.isInitialized){
                val temp = identifyKerawananLayers(_mapView.value!!,_mapUiState.value.currentPinLocation!!.point,_mapUiState.value.totalKRBLayerCount)
                _resultCardUiState.update {
                    it.copy(
                        identifiedLayerList = temp
                    )
                }
            }
        }

    }


    fun setInitMapView(mapView: MapView,multiplePermissionsState: MultiplePermissionsState?){

        Log.d(TEST_LOG,"viewtreeobserver = ${mapView.viewTreeObserver}")
        Log.d(TEST_LOG,"mapView Init? = ${_mapView.isInitialized}")
        if (mapView.viewTreeObserver.isAlive && !_mapView.isInitialized){
            _mapView.value = mapView
            _locationDisplay.value = mapView.locationDisplay

            if (multiplePermissionsState?.allPermissionsGranted == true){
                viewModelScope.launch {
                    var isFailed = true
                    do {
                        _locationDisplay.value?.dataSource?.start()?.onSuccess {
                            isFailed = false
                            updateToggleState(toggleList[0],multiplePermissionsState)
                        }?.onFailure{
                            Log.d(TEST_LOG,it.message.toString())
                            isFailed = true
                        }
                    }while (isFailed)
                }
                _inputCardUiState.update {
                    it.copy(
                        isLocationDisabled = false
                    )
                }
            }else{
                _inputCardUiState.update {
                    it.copy(
                        isLocationDisabled = true
                    )
                }
            }

            Log.d(TEST_LOG,"_mapView= ${_mapView.value} isInit:${_mapView.isInitialized}")
            Log.d(TEST_LOG,"_LocationDisplay= ${_locationDisplay.value!!.mapLocation} x:${_locationDisplay.value!!.mapLocation!!.x},y${_locationDisplay.value!!.mapLocation!!.y}")
        }
    }

    override fun onCleared() {
        super.onCleared()
    }



    fun resetInput(){
        _inputCardUiState.update {
            InputCardUiState(
                isLocationDisabled = _inputCardUiState.value.isLocationDisabled
            )
        }
        _resultCardUiState.update {
            ResultCardUiState()
        }

        _mapUiState.update {
            it.copy(
                isInputProcessNotDone = true
            )
        }



        if (_mapView.isInitialized){
            for (i in (1.._mapUiState.value.totalKRBLayerCount)){
                _mapUiState.value.map.operationalLayers.removeLast()
                Log.d(TEST_LOG,"Last Layer removed")
            }
        }
        removeLastPin()
    }

    fun setMapScale(zoomScale: Float) {
        val mapScale = (zoomScale.toDouble() * MAP_MAX_SCALE)
        viewModelScope.launch {
            if (_mapView.isInitialized){
                _mapView.value!!.setViewpointScale(mapScale)
                _mapScale.update {
                    _mapView.value!!.mapScale.value
                }
            }
        }
    }

    fun updateMapScale() {
        if(_mapView.isInitialized){
            _mapScale.update {
                _mapView.value!!.mapScale.value
            }
        }
    }


    init {
        setFaultLayer()
        setLocatorTask()
        fetchDataFromFirebaseDB()



    }

//    private fun selectCurrentGempa(index:Int){
//
//        when(_firebaseResponse.value){
//            is DataState.Success -> {
//
//            }
//            is DataState.Empty -> TODO()
//            is DataState.Failure -> TODO()
//            is DataState.Loading -> TODO()
//
//        }
//    }

    private fun fetchDataFromFirebaseDB() {
        val tempList = mutableListOf<Gempa>()
        _firebaseResponse.value = DataState.Loading
        FirebaseDatabase
            .getInstance()
            .getReference("Data/DataGempa")
            .orderByChild("DateTime")
            .limitToLast(10)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(FIREBASE_TEST,"firebase snapshot $snapshot")
                    for(data in snapshot.children){
                        val item = data.getValue(Gempa::class.java)
                        if(item != null){
                            tempList.add(item)
                            Log.d(FIREBASE_TEST,"fireitem added to templist ${item}")
                        }
                    }
                    if (tempList.isNotEmpty()){
                        _firebaseResponse.value = DataState.Success(tempList)
                        _latestResponse.update {
                            tempList.last()
                        }
                    }
                    Log.d(FIREBASE_TEST,"firebase snapshot size ${tempList.size}")
                    Log.d(FIREBASE_TEST,"firebase snapshot size ${tempList.map { it.DateTime } }")

                }

                override fun onCancelled(error: DatabaseError) {
                    _firebaseResponse.value = DataState.Failure(error.message)
                }
            })
    }


}



