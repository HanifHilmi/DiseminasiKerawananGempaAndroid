package com.hilmihanif.kerawanangempadantsunami.ui.screens.beranda

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.DataState
import com.hilmihanif.kerawanangempadantsunami.firebase.rtdb.Gempa
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel


@Composable
fun GempaM5List(
    result: DataState,
    viewModel: MainMapViewModel,
    mapControllerVisibile: (Boolean) -> Unit = {}
) {

    var isItemClicked by remember { mutableStateOf(false) }
    var gempaSelected by remember { mutableStateOf(Gempa()) }

    if (!isItemClicked) {
        when (result) {
            is DataState.Failure -> FailureBox(result.message)
            is DataState.Success -> {
                GempaDirasakanBox(
                    gempaList = result.data,
                    onItemClicked = {
                        gempaSelected = it
                        isItemClicked = true
                    }
                )
            }

            else -> LoadingBox()
        }
        mapControllerVisibile(false)
    } else {
        mapControllerVisibile(true)
        GempaSelectedCard(
            title = "",
            gempaData = gempaSelected,
            isBackIcon = true,
            onBackIconClick = { isItemClicked = false },
            modifier = Modifier.fillMaxWidth(),
            setGempaPin = {
                viewModel.setOnGempaPin(it)
            }
        )
        BackHandler(isItemClicked) {
            isItemClicked = false
        }
    }


}