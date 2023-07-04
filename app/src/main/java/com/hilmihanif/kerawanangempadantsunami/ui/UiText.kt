package com.hilmihanif.kerawanangempadantsunami.ui

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource

sealed class UiText{
    data class DynamicString(val value:String):UiText()
    class StringResource(
        @StringRes val resId:Int,
        vararg val args: Any
    ):UiText()

    data class DynamicArray(val arrayValue:Array<String>):UiText()
    class ArrayResource(
        @ArrayRes val resId:Int,
    ):UiText()



    @Composable
    fun asString(): String? {
        return when(this){
            is DynamicString -> value
            is StringResource -> stringResource(id = resId,*args)
            else -> {null}
        }
    }
    @Composable
    fun asArrayString(): Array<String>? {
        return when(this){
            is DynamicArray -> arrayValue
            is ArrayResource -> stringArrayResource(id = resId)
            else -> {null}
        }
    }

    fun asArrayString(context:Context): Array<String>? {
        return when(this){
            is DynamicArray -> arrayValue
            is ArrayResource -> context.resources.getStringArray(resId)
            else -> {null}
        }
    }



}
