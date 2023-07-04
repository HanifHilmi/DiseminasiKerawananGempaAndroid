package com.hilmihanif.kerawanangempadantsunami.screens.kerawanan

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.hilmihanif.kerawanangempadantsunami.R
import com.hilmihanif.kerawanangempadantsunami.ui.theme.FaultColor
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KerawananGempaDanTsunamiTheme
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbColor


@Composable
fun MapLegendDialog(
    modifier : Modifier = Modifier,
    onClose:() -> Unit = {},
) = AlertDialog(
    onDismissRequest = onClose,
    modifier = modifier,
    confirmButton = {},
    dismissButton = {
        TextButton(onClick = onClose) {
            Text(text = "Tutup")
        }
    },
    title = {
        Divider()
        Text(text = "Keterangan(Legenda)", style = MaterialTheme.typography.titleMedium)
    },
    text = {
        val stringList = stringArrayResource(id = R.array.keterangan_legenda).toList()

        val colorList = KrbColor.krbColorList

        LazyColumn {
            item {
                Row{
                    Icon(
                        imageVector =  ImageVector . vectorResource (id = R.drawable.ic_wavy_line),
                        contentDescription = "",
                        tint = FaultColor,
                    )
                    Text(
                        text = stringResource(id = R.string.keterangan_patahan),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            itemsIndexed(stringList){index,keterangan ->
                Divider()
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_square_24),
                        contentDescription = "",
                        tint = colorList[index]
                    )
                    Text(
                        text = keterangan,
                        style = MaterialTheme.typography.labelMedium
                    )


                }
            }

        }
    }

)

@Composable
fun LegendDialog(

) {
    var showDlg by remember { mutableStateOf(true)}
    Dialog(
        onDismissRequest = { showDlg = false },
        content = {
            Column(Modifier.background(Color.White)) {
                Text(
                    text = "Pick something from the list",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Row(

                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    TextButton(onClick = { showDlg = false }) {
                        Text("Cancel")
                    }
                    TextButton(onClick = { showDlg = false }) {
                        Text("Done")
                    }
                }
            }
        },
    )
}

@Preview(name = "Dark Mode",uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun DialogPrev() {
    KerawananGempaDanTsunamiTheme {
        MapLegendDialog()
    }
//    LegendDialog()
}
@Preview
@Composable
fun Dialog2Prev() {
    //MapLegendDialog()
    LegendDialog()
}

