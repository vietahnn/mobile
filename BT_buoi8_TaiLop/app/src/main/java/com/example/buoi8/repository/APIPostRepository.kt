package com.example.buoi8.repository

import com.example.buoi8.model.Post
import com.example.buoi8.model.RetrofitInstance
import retrofit2.Response

class APIPostRepository {

    suspend fun  getPosts(): Response<List<Post>> {
        return RetrofitInstance.apijsonPlaceholder.getPosts()
    }
}