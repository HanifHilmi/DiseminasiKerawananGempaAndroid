package com.hilmihanif.kerawanangempadantsunami.firebase.rtdb

sealed class DataState{
    class Success(val data:MutableList<Gempa>) :DataState()
    class Failure(val message:String) :DataState()
    object Loading : DataState()
    object Empty : DataState()
}
