package com.example.socialapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.socialapp.daos.UserDao
import com.example.socialapp.models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_in.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LogIn : AppCompatActivity() {
    private val RC_SIGN_IN: Int=89
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

       auth= Firebase.auth

       val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           .requestIdToken(getString(R.string.default_web_client_id))
           .requestEmail()
           .build()

       googleSignInClient=GoogleSignIn.getClient(this,gso)
    }

    override fun onStart() {
        super.onStart()
        updateUI(auth.currentUser)

    }

    fun signIn(view: View) {

        val signInIntent=googleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivity","reached!!")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account=task.getResult(ApiException::class.java)!!
                Log.d("successSignIn","okkkkk")
                 firebaseAuthWithGoogle(account.idToken!!)
            } catch(e:ApiException){
               Log.d("failedSignIn",e.toString())
            }

        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        button.visibility=View.GONE
        progressBar.visibility=View.VISIBLE
        Log.d("authWithGoogle","reached!!")
        val credential= GoogleAuthProvider.getCredential(idToken, null)

        GlobalScope.launch(Dispatchers.IO) {
            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                    updateUI(firebaseUser)
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        Log.d("updateUi","reached!!")
        if(firebaseUser!=null){
            val user= Users(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            val userDao=UserDao()
            userDao.addUser(user)
            val mainActivityIntent=Intent(this,MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else{
            button.visibility=View.VISIBLE
            progressBar.visibility=View.GONE
        }
    }
}