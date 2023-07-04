package com.hilmihanif.kerawanangempadantsunami.mapTools


import androidx.compose.ui.graphics.toArgb
import com.arcgismaps.Color
import com.arcgismaps.mapping.symbology.SimpleFillSymbol
import com.arcgismaps.mapping.symbology.SimpleFillSymbolStyle
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbColor

object KerawananSymbol {


    fun sangatRendah(): SimpleFillSymbol {
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbColor.KrbSangatRendah.toArgb()),
            null,
        )
    }
    fun rendah(): SimpleFillSymbol {
        return SimpleFillSymbol(SimpleFillSymbolStyle.Solid,
            Color(KrbColor.KrbRendah.toArgb()),
        null
        )
    }

    fun menengah(): SimpleFillSymbol {
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbColor.KrbSedang.toArgb()),

            )
    }

    fun tinggi():SimpleFillSymbol{
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbColor.KrbTinggi.toArgb()),
            null
        )
    }

    fun rombakan():SimpleFillSymbol{
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbColor.KrbRombakan.toArgb()),
            null
        )
    }

    fun others():SimpleFillSymbol{
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbColor.KrbRombakan.toArgb()),
            null
        )
    }

}


