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

    //val result = viewModel.firebaseResponse.collectAsState()
    val latestresult = viewModel.latestResponse.collectAsState()
    //GempaHistoryContent(result = result.value)


    

    val tabs = stringArrayResource(id = R.array.beranda_tabs).toList()

    var tabIndex by remember { mutableStateOf(0)}
    var shakemapDialog by remember { mutableStateOf(false)}

    
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
            modifier = Modifier.padding(),
            onShakemapClick = {
                shakemapDialog = true
            }
        ) {
            viewModel.removeAllGempaPin()
            when (tabIndex) {
                0 -> {
                    GempaTerkiniCard(
                        gempaData = latestresult.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (shakemapDialog){
                        ShakeMapDialog(url = latestresult.value.getShakemapUrl()) {
                            shakemapDialog = false
                        }
                    }
                    viewModel.setOnGempaPin(latestresult.value)
                }

                1 -> {

                }

                2 -> {

                }
            }
        }
    }


}











