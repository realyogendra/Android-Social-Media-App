package com.example.socialapp

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialapp.daos.PostDao
import com.example.socialapp.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IPostAdapter {

    private lateinit var postDao: PostDao
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener{
              startActivity(Intent(this,createPostActivity::class.java))
        }

        setUpRecyclerView()
    }

    override fun onStart() {
        super.onStart()
         adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.startListening()
    }

    private fun setUpRecyclerView() {

        postDao=PostDao()

        val postCollections=postDao.postCollection
        val query=postCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions=FirestoreRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()


        adapter=PostAdapter(recyclerViewOptions , this )

        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter=adapter

    }

    override fun onLikeClicked(postId: String) {
           postDao.updateLikes(postId)

    }
}