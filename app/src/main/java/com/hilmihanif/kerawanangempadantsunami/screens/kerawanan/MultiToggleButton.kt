package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hilmihanif.kerawanangempadantsunami.R

@Composable
fun MultiToggleButton(
    currentSelection:String,
    isLocationDisabled:(Boolean) = false,
    onToggleChange:(String)-> Unit
) {
    val selectedTint = MaterialTheme.colorScheme.primary
    val unselectedTint = Color.Unspecified
    val disabledTint =MaterialTheme.colorScheme.inverseSurface
    val toggleList = stringArrayResource(id = R.array.toggle_list)
    val localContext = LocalContext.current

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(5.dp))
    ) {
        toggleList.forEachIndexed { index, toggleState ->
            val isSelected = currentSelection.lowercase() == toggleState.lowercase()
            val backgroundTint = if (isSelected) selectedTint else unselectedTint
            val textColor = if(isSelected) Color.White else  Color.Unspecified

            var enabled = true

            if (index != 0) {
                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
            if (isLocationDisabled && index == 0) enabled = false

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(if (enabled) backgroundTint else disabledTint)
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .clickable {
                        if (!enabled) {
                            Toast
                                .makeText(
                                    localContext,
                                    "Fitur tidak tersedia, lokasi dinonaktifkan",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        }
                    }
                    .toggleable(
                        value = isSelected,
                        enabled = enabled,
                        onValueChange = { selected ->
                            if (selected) {
                                onToggleChange(toggleState)
                            }
                        },
                    )

                    .weight(.3f)
            ) {
                Text(
                    toggleState,
                    color = textColor,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium

                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMultiToggle() {
    val list = listOf("Koordinat saat Ini","Tap melalui peta","Input Koordinat")

    MultiToggleButton(currentSelection = list[1] , isLocationDisabled = true, onToggleChange = {})
}