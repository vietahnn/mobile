package com.example.buoi8.model

import retrofit2.Response
import retrofit2.http.GET

interface APIJSONPlaceholder {
    @GET ("/posts")
    suspend fun getPosts(): Response<List<Post>>
}