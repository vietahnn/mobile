package com.example.buoi8.model

import com.example.buoi8.CmmVariable.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apijsonPlaceholder: APIJSONPlaceholder by lazy{
        retrofit.create(APIJSONPlaceholder::class.java)
    }
}