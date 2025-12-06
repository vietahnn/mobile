package com.example.buoi8.model

import com.google.gson.annotations.SerializedName

data class Post (
    @SerializedName("userid") //ánh xạ "userid" (trong JSON) với userId
    val userId : Int,
    val id : Int,
    val title : String,
    val body : String
)