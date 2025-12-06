package com.example.buoi8.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buoi8.model.Post
import com.example.buoi8.repository.APIPostRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostViewModel(private val postRepository: APIPostRepository) : ViewModel() {
    val responseValue : MutableLiveData<Response<List<Post>>> = MutableLiveData()
//    fun getPostItem(){
//        viewModelScope.launch {
//            val response = postRepository.getPostItem()
//            responseValue.value = response
//        }
//    }
    fun getPosts(){
        viewModelScope.launch {
            val response = postRepository.getPosts()
            responseValue.value = response
        }
    }

}