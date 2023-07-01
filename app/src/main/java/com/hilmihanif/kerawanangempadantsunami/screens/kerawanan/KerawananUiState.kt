package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import androidx.compose.ui.text.input.TextFieldValue
import com.arcgismaps.LoadStatus
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.tasks.geocode.LocatorTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
    val identifiedLayerList:  List<Map<String, Any?>> = emptyList(),
    val selectedProv :String = "",
    val isLayerLoaded :Boolean = false,
    val gempaLoadStatus: StateFlow<LoadStatus> = MutableStateFlow(LoadStatus.NotLoaded),
    val gmLoadStatus: StateFlow<LoadStatus> = MutableStateFlow(LoadStatus.NotLoaded),
    val tsuLoadStatus: StateFlow<LoadStatus> = MutableStateFlow(LoadStatus.NotLoaded),
    val currentProvinsi :String = "",
)

data class MapUiState(
    val map: ArcGISMap,
    val currentGeocodeResult: Map<String,Any?> = mapOf(),
    val currentPinLocation: Location? = null,
    val isInputProcessNotDone:Boolean = true,
    val locatorStatus : LoadStatus = LoadStatus.NotLoaded,
    val locatorTask: LocatorTask? = null,
    val currentViewPoint : Viewpoint = Viewpoint(3.028, 98.905, 7e6),
    val currentMapStatusDesc: String = "",
    val totalKRBLayerCount:Int = 0

)