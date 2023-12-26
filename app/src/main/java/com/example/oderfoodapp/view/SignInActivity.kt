package com.example.oderfoodapp.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.oderfoodapp.R
import com.example.oderfoodapp.utils.Common
import com.example.oderfoodapp.databinding.ActivitySignInBinding
import com.example.oderfoodapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rengwuxian.materialedittext.MaterialEditText
import dmax.dialog.SpotsDialog
import io.paperdb.Paper

class SignInActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    var tableUser: DatabaseReference? = null
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)

        Paper.init(this)
        database = FirebaseDatabase.getInstance()
        tableUser = database.getReference("User")
        binding.txtForgotPwd.setOnClickListener { showForgotPwdDialog() }
        binding.btnSignIn.setOnClickListener(View.OnClickListener {
            if (Common.isConnectedToInternet(this)) {
                if (binding.cbRemember.isChecked) {
                    Paper.book().write(Common.USER_KEY, binding.edtphone.text.toString())
                    Paper.book().write(Common.PASSWORD_KEY, binding.edtPassword.text.toString())
                }
                val mDialog: AlertDialog = SpotsDialog(this@SignInActivity)
                mDialog.show()
                tableUser?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        if (dataSnapshot.child(binding.edtphone.text.toString()).exists()) {
                            mDialog.dismiss()
                            val user: User? = dataSnapshot.child(
                                binding.edtphone.text.toString()
                            ).getValue(
                                User::class.java
                            )
                            user?.phone = binding.edtphone.text.toString()
                            if (user?.password == binding.edtPassword.text.toString()) {
                                val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                                Common.currentUser = user
                                Log.e("=====user","$user")
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@SignInActivity,
                                    "Sign in failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            mDialog.dismiss()
                            Toast.makeText(
                                this@SignInActivity,
                                "User not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(
                            this@SignInActivity,
                            "Sign in failed \n ${databaseError.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                Toast.makeText(
                    this@SignInActivity,
                    "Please check your connection!!",
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            }
        })
    }

    private fun showForgotPwdDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Forgot Password")
        builder.setMessage("Enter your secure code")
        val inflater: LayoutInflater = this.layoutInflater
        val forgot_view: View = inflater.inflate(R.layout.forgot_password_layout, null)
        builder.setView(forgot_view)
        builder.setIcon(R.drawable.ic_security_black_24dp)
        val edtPhone: MaterialEditText =
            forgot_view.findViewById<View>(R.id.edtPhone) as MaterialEditText
        val edtSecureCode: MaterialEditText =
            forgot_view.findViewById<View>(R.id.edtSecureCode) as MaterialEditText
        builder.setPositiveButton("YES"
        ) { _, _ ->
            tableUser?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user: User? = dataSnapshot.child(edtPhone.text.toString())
                        .getValue(User::class.java)
                    if (user?.secureCode == edtSecureCode.text.toString()) Toast.makeText(
                        this@SignInActivity,
                        "Your password " + user.password,
                        Toast.LENGTH_LONG
                    ).show() else {
                        Toast.makeText(
                            this@SignInActivity,
                            "Wrong secure code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        builder.setNegativeButton(
            "NO"
        ) { _, _ -> }
        builder.show()
    }
}