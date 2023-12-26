package com.example.oderfoodapp.view

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common.isConnectedToInternet
import com.example.oderfoodapp.databinding.ActivitySignUpBinding
import com.example.oderfoodapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        val database = FirebaseDatabase.getInstance()
        val tableUser = database.getReference("User")
        binding.btnSignUp.setOnClickListener(View.OnClickListener {
            if (isConnectedToInternet(baseContext)) {
                val mDialog = ProgressDialog(this@SignUpActivity)
                mDialog.setMessage("Please waiting...")
                mDialog.show()
                tableUser.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.child(binding.edtPhone.text.toString()).exists()) {
                            mDialog.dismiss()
                            Toast.makeText(
                                this@SignUpActivity,
                                "Phone number already register",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            mDialog.dismiss()
                            val user = User(
                                binding.edtName.text.toString(),
                                binding.edtPassword.text.toString(),
                                binding.edtSecureCode.text.toString()
                            )
                            tableUser.child(binding.edtPhone.text.toString()).setValue(user)
                            Toast.makeText(
                                this@SignUpActivity,
                                "Sign up successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            } else {
                Toast.makeText(
                    this@SignUpActivity,
                    "Please check your connection!!",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
        })
    }
}