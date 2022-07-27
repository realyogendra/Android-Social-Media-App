package com.example.socialapp.daos

import com.example.socialapp.models.Post
import com.example.socialapp.models.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    val db= FirebaseFirestore.getInstance()
    val postCollection=db.collection("posts")
    val auth= Firebase.auth
    fun addPost(text:String){

        val currentUserId =auth.currentUser!!.uid

        GlobalScope.launch {
            val userDao=UserDao()
            val user=userDao.getUserById(currentUserId).await().toObject(Users::class.java)!!

            val currentTime=System.currentTimeMillis()

            val post= Post(text,user,currentTime)

            postCollection.document().set(post)
        }

    }
    fun getPostById(postId:String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }

    fun updateLikes(posId: String){


        GlobalScope.launch {

            val currentUserId=auth.currentUser!!.uid
            val post = getPostById(posId).await().toObject(Post::class.java)!!
            val isLiked=  post.likedBy.contains(currentUserId)

            if(isLiked){
                post.likedBy.remove(currentUserId)

            }
            else{
                post.likedBy.add(currentUserId)
            }
            postCollection.document(posId).set(post)
        }
    }
}