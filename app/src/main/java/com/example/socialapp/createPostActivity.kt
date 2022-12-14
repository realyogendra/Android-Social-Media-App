package com.example.socialapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.socialapp.daos.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*

class createPostActivity : AppCompatActivity() {

    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        postDao=PostDao()
        button2.setOnClickListener{
            val input=postInput.text.toString()
            if(input.isNotEmpty()){
                postDao.addPost(input)
                finish()
            }
        }
    }
}