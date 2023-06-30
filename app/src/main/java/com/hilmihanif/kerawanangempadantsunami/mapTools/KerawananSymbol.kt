package com.hilmihanif.kerawanangempadantsunami.mapTools


import androidx.compose.ui.graphics.toArgb
import com.arcgismaps.Color
import com.arcgismaps.mapping.symbology.SimpleFillSymbol
import com.arcgismaps.mapping.symbology.SimpleFillSymbolStyle
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbRendah
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbRombakan
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbSangatRendah
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbSedang
import com.hilmihanif.kerawanangempadantsunami.ui.theme.KrbTinggi

object KerawananSymbol {


    fun sangatRendah(): SimpleFillSymbol {
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            //Color(context.getColor(R.color.krb_sangatrendah)) ,
            Color(KrbSangatRendah.toArgb()),
            null,
        )
    }
    fun rendah(): SimpleFillSymbol {
        return SimpleFillSymbol(SimpleFillSymbolStyle.Solid,
        Color(KrbRendah.toArgb()),
        null
        )
    }

    fun menengah(): SimpleFillSymbol {
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbSedang.toArgb()),

            )
    }

    fun tinggi():SimpleFillSymbol{
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbTinggi.toArgb()),
            null
        )
    }

    fun rombakan():SimpleFillSymbol{
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbRombakan.toArgb()),
            null
        )
    }

    fun others():SimpleFillSymbol{
        return SimpleFillSymbol(
            SimpleFillSymbolStyle.Solid,
            Color(KrbRombakan.toArgb()),
            null
        )
    }

}


