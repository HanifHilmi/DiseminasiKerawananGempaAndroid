package com.hilmihanif.kerawanangempadantsunami.screens.beranda


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.screens.main_map.MapControllerScreen
import com.hilmihanif.kerawanangempadantsunami.viewmodels.MainMapViewModel


@Composable
fun BerandaScreen(viewModel: MainMapViewModel) {

    val result = viewModel.firebaseResponse.collectAsState()
    val latestresult = viewModel.latestResponse.collectAsState()
    //GempaHistoryContent(result = result.value)

    

    val tabs = stringArrayResource(id = R.array.beranda_tabs).toList()

    var tabIndex by remember { mutableStateOf(0)}
    var showShakemapDialog by remember { mutableStateOf(false)}
    var controllerVisibility by remember { mutableStateOf(true)}


    
    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(selectedTabIndex = tabIndex)  {
            tabs.forEachIndexed { index, title -> 
                Tab(
                    text = { Text(text = title)},
                    selected = tabIndex == index, 
                    onClick = { tabIndex = index }
                ) 
            }
        }
        MapControllerScreen(
            currentScreen = "BerandaTab$tabIndex",
            viewModel = viewModel,
            visibility = controllerVisibility,
            modifier = Modifier.padding(),
            onShakemapClick = {
                showShakemapDialog = true
            }
        ) {
            var currentShakemapUrl by remember { mutableStateOf("")}
            if (showShakemapDialog && currentShakemapUrl.isNotEmpty()){
                ShakeMapDialog(url = currentShakemapUrl) {
                    showShakemapDialog = false
                }
            }
            viewModel.removeAllGempaPin()
            when (tabIndex) {
                0 -> {
                    controllerVisibility = true
                    GempaSelectedCard(
                        title = "Gempa Terkini",
                        gempaData = latestresult.value,
                        modifier = Modifier.fillMaxWidth(),
                        setGempaPin = {
                            viewModel.setOnGempaPin(it)
                            currentShakemapUrl = it.getShakemapUrl()
                        }
                    )
                }

                1 -> {
                    //controllerVisibility = false
                    GempaDirasakanList(
                        result = result.value,
                        viewModel = viewModel,
                        currentShakemapurl = {url ->currentShakemapUrl = url},
                        mapControllerVisibile = {selected-> controllerVisibility = selected }
                    )

                }

                2 -> {
                    controllerVisibility = true
                }
            }
        }
    }


}










