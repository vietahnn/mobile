package com.example.buoi8.view

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.buoi8.R
import com.example.buoi8.repository.APIPostRepository
import com.example.buoi8.viewmodel.PostViewModel
import com.example.buoi8.viewmodel.PostViewModelFactory


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val listView = findViewById<ListView>(R.id.listView)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        val viewModelFactory = PostViewModelFactory(APIPostRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(PostViewModel::class.java)
        viewModel.getPosts()
        viewModel.responseValue.observe(this) { response ->
            if (response.isSuccessful) {
                var dem = 0

                val postItem = response.body()
                var listPostItem = ArrayList<String>()

                for (item in postItem!!) {
                    dem++
                    val id = item.id
                    val title = item.title
                    listPostItem.add(id.toString() + " " + title)


                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listPostItem)
                listView.adapter = adapter

            }else {
                Log.d("MainActivity", "Error: ${response.code()}" )
            }
        }

    }
}