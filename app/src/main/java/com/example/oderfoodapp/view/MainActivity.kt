package com.example.oderfoodapp.view

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.databinding.ActivityMainBinding
import com.example.oderfoodapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.paperdb.Paper
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var database: FirebaseDatabase
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val typeface: Typeface = Typeface.createFromAsset(assets, "fonts/NABILA.TTF")
        binding.txtSlogan.typeface = typeface
        Paper.init(this)
        binding.btnSignIn.setOnClickListener {
            val i = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(i)
        }
        binding.btnSignup.setOnClickListener {
            val i = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(i)
        }
        val user: String = Paper.book().read(Common.USER_KEY) ?: ""
        val pwd: String = Paper.book().read(Common.PASSWORD_KEY) ?: ""
        if (user.isNotEmpty() && pwd.isNotEmpty()) login(user, pwd)
    }

    private fun login(phone: String, pwd: String) {
        val tableUser: DatabaseReference = database.getReference("User")
        if (Common.isConnectedToInternet(this)) {
            val mDialog = ProgressDialog(this@MainActivity)
            mDialog.setMessage("Please waiting...")
            mDialog.show()
            Log.e("=====","$tableUser")
            tableUser.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e("=====dataSnapshot","$dataSnapshot")
                    if (dataSnapshot.child(phone).exists()) {
                        mDialog.dismiss()
                        val user: User? = dataSnapshot.child(phone).getValue(
                            User::class.java
                        )
                        user?.phone = phone
                        if (user?.password == pwd) {
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            Common.currentUser = user
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@MainActivity, "Sign in failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        mDialog.dismiss()
                        Toast.makeText(this@MainActivity, "User not exist", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        } else {
            Toast.makeText(this@MainActivity, "Please check your connection!!", Toast.LENGTH_SHORT)
                .show()
            return
        }
    }
}